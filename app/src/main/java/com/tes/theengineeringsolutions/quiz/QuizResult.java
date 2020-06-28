package com.tes.theengineeringsolutions.quiz;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tes.theengineeringsolutions.Adapters.ResultRecyclerView;
import com.tes.theengineeringsolutions.Models.LocalTestDatabase;
import com.tes.theengineeringsolutions.R;
import com.tes.theengineeringsolutions.utils.SharedPrefsUtils;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.util.ArrayList;
import java.util.List;

public class QuizResult extends AppCompatActivity {


    private RecyclerView testRecyclerView;
    private List<LocalTestDatabase> testList;
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

        testList = new ArrayList<>();
        populateTestList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        initializeFields();
        initRecyclerView();

        int[] decisions = calculateCorrect();
        int totalQuestion = LocalTestDatabase.listAll(LocalTestDatabase.class).size();
        float percent = ((((float) decisions[0]) / totalQuestion)) * 100;

        if (totalQuestion > 0) {
            textViewPercentage.setText(((int) percent) + "%");
            sectorProgressView.setPercent(percent);
        } else {
            textViewPercentage.setText(0 + "%");
            sectorProgressView.setPercent(0);
        }
        int questionAnswered = SharedPrefsUtils.preferenceSize(this);
        textViewTotalScore.setText(decisions[0] + "/" + questionAnswered);
        textViewChecklist.setText("checklist " + decisions[0] + "/" + questionAnswered + " " + ((int) percent));
    }

    private void initRecyclerView() {
        ResultRecyclerView recyclerViewAdapter = new ResultRecyclerView(this, testList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        testRecyclerView.setLayoutManager(layoutManager);
        testRecyclerView.setAdapter(recyclerViewAdapter);
    }

    private int[] calculateCorrect() {
        int[] countNumberOfCorrectAndIncorrectQuestions = {0, 0};

        for (LocalTestDatabase currentQuestion : testList) {
            int answer = SharedPrefsUtils.getIntegerPreference(this, currentQuestion.getQuestionNo() + "", -1);
            if (answer != -1 && answer == currentQuestion.getAnswer())
                countNumberOfCorrectAndIncorrectQuestions[0] = countNumberOfCorrectAndIncorrectQuestions[0] + 1;
            else
                countNumberOfCorrectAndIncorrectQuestions[1] = countNumberOfCorrectAndIncorrectQuestions[1] + 1;
        }
        return countNumberOfCorrectAndIncorrectQuestions;
    }

    private void populateTestList() {
        for (String questionNumber : SharedPrefsUtils.keysList(this)) {
            List<LocalTestDatabase> localTestDatabase = LocalTestDatabase.findWithQuery(LocalTestDatabase.class, "SELECT * FROM LOCAL_TEST_DATABASE WHERE QUESTION_NO == ?", questionNumber);
            testList.addAll(localTestDatabase);
        }
    }

}
