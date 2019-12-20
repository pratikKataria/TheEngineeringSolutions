package com.tes.theengineeringsolutions.Models;

public class QuizContract {

    private String display_name, file_uri, file_name, date, password, number_of_questions, test_duration, subject_code;
    private String result;
    private String percentage;
    private String correct;
    private String incorrect;
    private String color;
    private String badge;

    public QuizContract() {
        //required
    }

    public QuizContract(String display_name, String file_uri, String file_name, String password, String number_of_questions, String test_duration, String subject_code, String date, String color) {
        this.display_name = display_name;
        this.file_uri = file_uri;
        this.file_name = file_name;
        this.password = password;
        this.number_of_questions = number_of_questions;
        this.test_duration = test_duration;
        this.subject_code = subject_code;
        this.date = date;
        this.color = color;
    }

    public QuizContract(String display_name, String subject_code, String date, String result, String percentage, String correct, String incorrect, String number_of_questions, String color, String badge) {
        this.display_name = display_name;
        this.subject_code = subject_code;
        this.date = date;
        this.result = result;
        this.percentage = percentage;
        this.correct = correct;
        this.incorrect = incorrect;
        this.number_of_questions = number_of_questions;
        this.color = color;
        this.badge = badge;
    }

    public String getTotalQuestion() {
        return number_of_questions;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getIncorrect() {
        return incorrect;
    }

    public void setIncorrect(String incorrect) {
        this.incorrect = incorrect;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getFile_uri() {
        return file_uri;
    }

    public String getDate() {
        return date;
    }

    public String getPassword() {
        return password;
    }

    public String getNumber_of_questions() {
        return number_of_questions;
    }

    public String getTest_duration() {
        return test_duration;
    }

    public String getSubject_code() {
        return subject_code;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }
}
