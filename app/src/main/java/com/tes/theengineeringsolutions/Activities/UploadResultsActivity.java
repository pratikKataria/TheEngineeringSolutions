package com.tes.theengineeringsolutions.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.tes.theengineeringsolutions.Models.ConnectivityReceiver;
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

import static com.tes.theengineeringsolutions.Models.ConnectivityReceiver.isConnected;

public class UploadResultsActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    private static final int[] colors = {
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
    private final Date date = new Date();
    HashMap<Integer, Integer> questionAnswered;
    List<LocalTestDatabase> questionList;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar;
    private MaterialButton materialButton;
    private String subjectCode;
    private String subject;
    private String stringDate;
    private int totalQuestion;

    private void init_fields() {
        progressBar = findViewById(R.id.progressbar);
        materialButton = findViewById(R.id.uploadResult_mb_retry);
        firebaseFirestore = FirebaseFirestore.getInstance();

        questionList = new ArrayList<>();

        subjectCode = getIntent().getStringExtra("TEST_CODE");
        subject = getIntent().getStringExtra("SUBJECT_NAME");
        totalQuestion = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);
        stringDate = new SimpleDateFormat("EE").format(date) + "-" + new SimpleDateFormat("dd").format(date) + "-" + new SimpleDateFormat("MM").format(date) + "-" + new SimpleDateFormat("YYYY").format(date);


        populateMap();
        populateTestList();
        animation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_results);

        checkConnection();

        init_fields();

        int[] answers = checkAnswers(questionAnswered);
        float percent = ((((float) answers[0]) / totalQuestion)) * 100;

        Random random = new Random();



       Map<String, String>data = build_data(subject, subjectCode, stringDate, answers[0], answers[1], totalQuestion, questionAnswered.size(), colors[random.nextInt(11)], percent);
        Map<String, Object> header = new HashMap<>();
        header.put(subjectCode, data);

        if (FirebaseAuth.getInstance().getUid() != null) {
            uploadResult(header);
        }
    }

    private Map<String, String> build_data(String subject, String subjectCode, String date, int  questionCorrect, int questionIncorrect, int totalQuestions, int questionAnswered, int color, float percent) {
        Map<String, String> fields = new HashMap<>();
        fields.put("subject", subject);
        fields.put("subject_code", subjectCode);
        fields.put("date", date);
        fields.put("score", questionCorrect + "");
        fields.put("question_correct", questionCorrect + "");
        fields.put("questions_incorrect",questionIncorrect + "");
        fields.put("total_questions", totalQuestions + "");
        fields.put("questions_unanswered", (totalQuestions - questionAnswered) + "");
        fields.put("color", color+"");
        if (percent > 20) fields.put("result", "pass");
        else fields.put("result", "fail");

        if (percent > 0 && percent < 10) fields.put("percentage", "0" + ((int) percent));
        else fields.put("percentage", ((int) percent) + "");

        return fields;
    }

    private void uploadResult(Map<String, Object> header) {
        DocumentReference documentReference = firebaseFirestore.collection("Results").document(FirebaseAuth.getInstance().getUid());
        documentReference.set(header, SetOptions.merge()).addOnCompleteListener(task -> {
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
                            Map<String, Object> reupload = header;
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
        snackbar.setBehavior(new NoSwipeBehavior());
        snackbar.show();
    }
    class NoSwipeBehavior extends BaseTransientBottomBar.Behavior {
        @Override
        public boolean canSwipeDismissView(View child) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to exit ?");
        builder.setMessage("You will loose your score if you exit");
        builder.setPositiveButton("YES", (dialog, which) -> UploadResultsActivity.super.onBackPressed())
                .setPositiveButton("NO", (dialog, which) -> {});
    }
}