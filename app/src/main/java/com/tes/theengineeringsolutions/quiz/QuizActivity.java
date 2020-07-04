package com.tes.theengineeringsolutions.quiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.tes.theengineeringsolutions.roomdatabase.LocalTestDatabaseDoa;
import com.tes.theengineeringsolutions.Models.QuestionModel;
import com.tes.theengineeringsolutions.roomdatabase.QuizDatabase;
import com.tes.theengineeringsolutions.R;
import com.tes.theengineeringsolutions.utils.SharedPrefsUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

/*
 * created by pratik katariya
 * on 5/11/2019
 * updated on 6/11/19
 * updated on 7/11/19
 * updated on 10/11/2019
 * updated on 25/06/2020
 */
public class QuizActivity extends AppCompatActivity {

    private TextView testViewTestName;
    private TextView testViewTestCode;
    private TextView testViewQuestions;
    private TextView textViewQuestionAnswered;
    private TextView textViewQuestionRemaining;
    private TextView textViewQuestionCount;
    private RadioGroup choicesGroup;
    private MaterialButton nextBtn;
    private MaterialButton previousBtn;

    private int questionNumber;
    private int numberOfQuestionContainsInList;
    private List<QuestionModel> questionList;

    private void initFields() {
        testViewQuestions = findViewById(R.id.testSession_tv_question);
        textViewQuestionCount = findViewById(R.id.testSession_tv_question_count);
        textViewQuestionAnswered = findViewById(R.id.quizActivity_tv_answered);
        textViewQuestionRemaining = findViewById(R.id.quizActivity_tv_remaining);
        testViewTestName = findViewById(R.id.testSession_tv_test_name);
        testViewTestCode = findViewById(R.id.testSession_tv_test_code);
        choicesGroup = findViewById(R.id.radioGroup);
        nextBtn = findViewById(R.id.testSession_mb_next);
        previousBtn = findViewById(R.id.testSession_mb_previous);
        MaterialButton submitMb = findViewById(R.id.testSession_mb_submit);
        submitMb.setOnClickListener(n -> showSubmitDialog());
        TextView textViewTime = findViewById(R.id.textView_time);

        testViewTestName.setText(getIntent().getStringExtra("TEST_NAME"));
        testViewTestCode.setText(getIntent().getStringExtra("TEST_CODE"));
        textViewTime.setText(getIntent().getStringExtra("TEST_TOTAL_QUESTION").substring(0,2)+"  \u2022  "+ getIntent().getStringExtra("TEST_DURATION").substring(0,2) +" MINS");


        QuizDatabase quizDatabase = QuizDatabase.getInstance(this);
        LocalTestDatabaseDoa localTestDatabaseDoa = quizDatabase.testDatabaseDoa();
        try {
            questionList = new QuestionList(localTestDatabaseDoa).execute().get();
            numberOfQuestionContainsInList = questionList.size();
            Log.e(QuizActivity.class.getName(), questionList.size() + " ");
            Log.e(QuizActivity.class.getName(), questionList + " ");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class QuestionList extends AsyncTask<Void, Void, List<QuestionModel>> {

        LocalTestDatabaseDoa localTestDatabaseDoa;

        public QuestionList(LocalTestDatabaseDoa localTestDatabaseDoa) {
            this.localTestDatabaseDoa = localTestDatabaseDoa;
        }


        @Override
        protected List<QuestionModel> doInBackground(Void... voids) {
            return localTestDatabaseDoa.getAllQuestionsAndOptions();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_activity);

        initFields();

        if (!questionList.isEmpty()) {
            setQuestionAndChoices();
            updateQuizProgressStatus();
        }

        choicesGroup.setOnCheckedChangeListener(onAnswerCheckedListener);

        nextBtn.setOnClickListener(v -> {
            if (questionNumber + 1 == numberOfQuestionContainsInList) {
                showSubmitDialog();
                return;
            }

            if (questionNumber < numberOfQuestionContainsInList - 1) {
                choicesGroup.clearCheck();
                questionNumber++;
                setQuestionAndChoices();
                setSavedChoiceSelection();
                textViewQuestionCount.setText("Question: " + (questionNumber + 1) + "/" + numberOfQuestionContainsInList);
            }
        });

        previousBtn.setOnClickListener(v -> {
            if (questionNumber > 0) {
                choicesGroup.clearCheck();
                questionNumber--;
                setQuestionAndChoices();
                setSavedChoiceSelection();
                textViewQuestionCount.setText("Question: " + (questionNumber + 1) + "/" + numberOfQuestionContainsInList);
            }
        });
    }

    RadioGroup.OnCheckedChangeListener onAnswerCheckedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton radioButton = findViewById(group.getCheckedRadioButtonId());
            if (radioButton != null) {
                Log.e(QuizActivity.class.getName(), "selection changed");
                SharedPrefsUtils.setIntegerPreference(QuizActivity.this, Integer.toString(questionNumber + 1), group.indexOfChild(radioButton) + 1);
                updateQuizProgressStatus();
            }
        }
    };

    public void setQuestionAndChoices() {
        QuestionModel questionModel = questionList.get(questionNumber);
        if (questionModel == null) return;
        testViewQuestions.setText("Q" + questionModel.getQuestionNo() + ". " + questionModel.getQuestions());
        ((RadioButton) choicesGroup.getChildAt(0)).setText(questionModel.getChoice1());
        ((RadioButton) choicesGroup.getChildAt(1)).setText(questionModel.getChoice2());
        ((RadioButton) choicesGroup.getChildAt(2)).setText(questionModel.getChoice3());
        ((RadioButton) choicesGroup.getChildAt(3)).setText(questionModel.getChoice4());
    }

    private void updateQuizProgressStatus() {
        int answeredQuestions = SharedPrefsUtils.preferenceSize(this);
        textViewQuestionAnswered.setText("Answered: " + answeredQuestions);
        textViewQuestionRemaining.setText("Remaining: " + (numberOfQuestionContainsInList - answeredQuestions) + "/" + numberOfQuestionContainsInList);
    }

    private void setSavedChoiceSelection() {
        int answerChoice = SharedPrefsUtils.getIntegerPreference(this, Integer.toString(questionNumber + 1), -1);
        if (answerChoice != -1) {
            RadioButton radioButton = (RadioButton) choicesGroup.getChildAt(answerChoice - 1);
            radioButton.setChecked(true);
        }
    }

    private void showSubmitDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("would you like to submit questions? ")
                .setMessage("submitting would show the result? ")
                .setPositiveButton("submit", dialogPositiveButtonClickListener)
                .setNegativeButton("cancel", (dialog, which) -> {
                });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to exit? ");
        builder.setMessage("Existing will loose your score")
                .setPositiveButton("submit", dialogPositiveButtonClickListener)
                .setNegativeButton("Exit", (dialog, id) -> QuizActivity.super.onBackPressed())
                .setNeutralButton("Cancel", (dialog, which) -> {
                });
        builder.show();
    }

    DialogInterface.OnClickListener dialogPositiveButtonClickListener = (dialog, which) -> {
        if (SharedPrefsUtils.preferenceSize(this) > 0)
            showIntent();
        else
            Toast.makeText(this, "must select one question", Toast.LENGTH_SHORT).show();
    };


    private void showIntent() {
        Intent intent = new Intent(QuizActivity.this, UploadResultsActivity.class);
        intent.putExtra("TEST_CODE", testViewTestCode.getText().toString());
        intent.putExtra("SUBJECT_NAME", testViewTestName.getText().toString());
        intent.putExtra("TOTAL_QUESTIONS", questionList.size());
        startActivity(intent);
        finish();
    }
}
