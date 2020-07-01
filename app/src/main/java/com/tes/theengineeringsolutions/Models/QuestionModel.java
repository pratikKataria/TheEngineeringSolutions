package com.tes.theengineeringsolutions.Models;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions_options")
public class QuestionModel {

    @PrimaryKey
    private int questionNo;
    private String questions;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private int answer;
    boolean isCompleted;

    public QuestionModel() {
        //required
    }

    public QuestionModel(int questionNo, String questions, String choice1, String choice2, String choice3, String choice4, int answer, boolean isCompleted) {
        this.questionNo = questionNo;
        this.questions = questions;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.answer = answer;
        this.isCompleted = isCompleted;
    }


    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(int questionNo) {
        this.questionNo = questionNo;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getChoice1() {
        return choice1;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public String getChoice3() {
        return choice3;
    }

    public void setChoice3(String choice3) {
        this.choice3 = choice3;
    }

    public String getChoice4() {
        return choice4;
    }

    public void setChoice4(String choice4) {
        this.choice4 = choice4;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof QuestionModel) {
            QuestionModel questionModel = (QuestionModel) obj;
            return questionModel.getQuestionNo() == questionNo;
        }
        return false;
    }
}
