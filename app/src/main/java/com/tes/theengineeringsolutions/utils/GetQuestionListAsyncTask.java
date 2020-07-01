package com.tes.theengineeringsolutions.utils;

import android.os.AsyncTask;

import com.tes.theengineeringsolutions.LocalTestDatabaseDoa;
import com.tes.theengineeringsolutions.Models.QuestionModel;

import java.util.List;

public class GetQuestionListAsyncTask extends AsyncTask<Void, Void, List<QuestionModel>> {
    private LocalTestDatabaseDoa localTestDatabaseDoa;

    public GetQuestionListAsyncTask(LocalTestDatabaseDoa localTestDatabaseDoa) {
        this.localTestDatabaseDoa = localTestDatabaseDoa;
    }

    @Override
    protected List<QuestionModel> doInBackground(Void... voids) {
        return localTestDatabaseDoa.getAllQuestionsAndOptions();
    }
}
