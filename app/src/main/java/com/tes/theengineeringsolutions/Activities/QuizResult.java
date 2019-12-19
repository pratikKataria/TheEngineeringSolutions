package com.tes.theengineeringsolutions.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tes.theengineeringsolutions.Adapters.ResultRecyclerView;
import com.tes.theengineeringsolutions.Models.LocalTestDatabase;
import com.tes.theengineeringsolutions.R;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuizResult extends AppCompatActivity {

    private HashMap<Integer, Integer> questionAnswered;

    private RecyclerView testRecyclerView;
    private List<LocalTestDatabase> testList;
    private ResultRecyclerView recyclerViewAdapter;
    private TextView textViewChecklist;
    private TextView textViewPercentage;
    private TextView textViewTotalScore;
    private ColorfulRingProgressView sectorProgressView;

    private void initializeFields() {
        testRecyclerView = findViewById(R.id.actQuizResult_question_list);
        textViewChecklist = findViewById(R.id.actQuizResult_tv_check_list);
        textViewPercentage = findViewById(R.id.actQuizResult_tv_percentage);
        textViewTotalScore = findViewById(R.id.actQuizResult_tv_score);
        sectorProgressView = findViewById(R.id.crpv);


        questionAnswered = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("QUESTION_ANSWERED");
        testList = new ArrayList<>();

        recyclerViewAdapter = new ResultRecyclerView(this, testList, questionAnswered);
        populateTestList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        initializeFields();
        questionAnswered.forEach((key, value) -> {
            Log.e("QUIZRESULT HASHMAP", "Key : " + key + " Value : " + value);
        });

        recyclerViewAdapter.notifyDataSetChanged();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        testRecyclerView.setLayoutManager(layoutManager);
        testRecyclerView.setAdapter(recyclerViewAdapter);


        int[] decisions = calculateCorrect(questionAnswered);
        int totalQuestion = LocalTestDatabase.listAll(LocalTestDatabase.class).size();
        float percent = ((((float) decisions[0]) / totalQuestion)) * 100;

        if (totalQuestion > 0) {
            textViewPercentage.setText(((int) percent) + "%");
            sectorProgressView.setPercent(percent);
        } else {
            textViewPercentage.setText(0 + "%");
            sectorProgressView.setPercent(0);
        }
        textViewTotalScore.setText(decisions[0] + "/" + questionAnswered.size());
        textViewChecklist.setText("checklist " + decisions[0] + "/" + questionAnswered.size() + " " + ((int) percent));
    }

    private int[] calculateCorrect(HashMap<Integer, Integer> qAnswered) {
        int[] decisions = {0, 0};
        for (LocalTestDatabase q : testList) {
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
            Log.d("QUIZ RESULT", "question number" + questionNumber);
            List<LocalTestDatabase> localTestDatabase = LocalTestDatabase.findWithQuery(LocalTestDatabase.class, "SELECT * FROM LOCAL_TEST_DATABASE WHERE QUESTION_NO == ?", questionNumberString);
            testList.addAll(localTestDatabase);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

}
