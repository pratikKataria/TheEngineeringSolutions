package com.tes.theengineeringsolutions.Activities;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class UploadResultsActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static Map<Integer, String> colors = new HashMap<>();
    private final Date date = new Date();
    HashMap<Integer, Integer> questionAnswered;
    List<LocalTestDatabase> questionList;
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
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar;
    private MaterialButton materialButton;
    private String subjectCode;
    private String subject;
    private String stringDate;
    private int totalQuestion;
    private boolean isPass = false;


    private void init_fields() {
        progressBar = findViewById(R.id.progressbar);
        materialButton = findViewById(R.id.uploadResult_mb_retry);
        firebaseFirestore = FirebaseFirestore.getInstance();

        questionList = new ArrayList<>();

        subjectCode = getIntent().getStringExtra("TEST_CODE");
        subject = getIntent().getStringExtra("SUBJECT_NAME");
        totalQuestion = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);
        stringDate = new SimpleDateFormat("EE").format(date) + "-" + new SimpleDateFormat("dd").format(date) + "-" + new SimpleDateFormat("MM").format(date) + "-" + new SimpleDateFormat("YYYY").format(date);


        colors.put(R.color.orange, "#E97939");
        colors.put(R.color.green, "#607F55");
        colors.put(R.color.darkBrown, "#3C3B1D");
        colors.put(R.color.lightGrey, "#596164");
        colors.put(R.color.greyMaterial, "#838294");
        colors.put(R.color.darkMaterial, "#252831");

        colors.put(R.color.skin, "#F8C9B5");
        colors.put(R.color.blue, "#79CBE8");
        colors.put(R.color.dullSkin, "#D1A38B");
        colors.put(R.color.AliceBlue, "#B4DDF6");
        colors.put(R.color.chipWrongColor, "#FFD8D8");
        colors.put(R.color.lightGreenMaterial, "#BDD8AF");

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
        if (percent > 25) isPass = true;

        Random random = new Random();


        Map<String, String> data = build_data(subject, subjectCode, stringDate, answers[0], answers[1], totalQuestion, questionAnswered.size(), colors.get(keys[random.nextInt(11)]), percent, "no badge");
        Map<String, Object> header = new HashMap<>();
        header.put(subjectCode, data);

        if (FirebaseAuth.getInstance().getUid() != null) {
            uploadResult(header);
        }
    }

    private Map<String, String> build_data(String subject, String subjectCode, String date, int questionCorrect, int questionIncorrect, int totalQuestions, int questionAnswered, String color, float percent, String badge) {
        Map<String, String> fields = new HashMap<>();
        fields.put("subject", subject);
        fields.put("subject_code", subjectCode);
        fields.put("date", date);
        fields.put("score", questionCorrect + "");
        fields.put("question_correct", questionCorrect + "");
        fields.put("questions_incorrect", questionIncorrect + "");
        fields.put("total_questions", totalQuestions + "");
        fields.put("questions_unanswered", (totalQuestions - questionAnswered) + "");
        fields.put("color", color + "");
        fields.put("badge", badge);
        if (percent > 25) fields.put("result", "pass");
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
                setProgress(isPass);
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

    private void setProgress(boolean isPass) {
        if (isPass) {
            Map<String, Object> header = new HashMap<>();
            Map<String, Integer> data = new HashMap<>();
            DocumentReference reference = FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getUid());
            reference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data1 = documentSnapshot.getData();
                        if (data1 != null && data1.containsKey("test_progress")) {
                            if (((Map<String, Object>) data1.get("test_progress")).containsKey(stringDate.substring(7))) {
                                long number = (Long) ((Map<String, Object>) data1.get("test_progress")).get(stringDate.substring(7));
                                data.put(stringDate.substring(7), ((int) number + 1));
                                header.put("test_progress", data);
                                reference.set(header, SetOptions.merge());
                                Toast.makeText(this, "progress upgraded", Toast.LENGTH_SHORT).show();
                            } else {
                                data.put(stringDate.substring(7), 0);
                                header.put("test_progress", data);
                                reference.set(header, SetOptions.merge());
                                Toast.makeText(this, "progress set", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            data.put(stringDate.substring(7), 0);
                            header.put("test_progress", data);
                            reference.set(header, SetOptions.merge());
                            Toast.makeText(this, "progress set", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    String e = task.getException().getMessage();
                    Log.e("TESTFRAGMENT", e);
                    Toast.makeText(this, "failed to set progress", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TESTFRAGMENT", e.getMessage());
                }
            });
        } else {
            Toast.makeText(this, "fail in test", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to exit ?");
        builder.setMessage("You will loose your score if you exit");
        builder.setPositiveButton("YES", (dialog, which) -> UploadResultsActivity.super.onBackPressed())
                .setPositiveButton("NO", (dialog, which) -> {
                });
    }

    class NoSwipeBehavior extends BaseTransientBottomBar.Behavior {
        @Override
        public boolean canSwipeDismissView(View child) {
            return false;
        }
    }
}
