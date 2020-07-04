package com.tes.theengineeringsolutions.roomdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.tes.theengineeringsolutions.Models.QuestionModel;

import java.util.List;

@Dao
public interface LocalTestDatabaseDoa {

    @Insert
    void insert(QuestionModel questionModel);

    @Update
    void update(QuestionModel questionModel);

    @Delete
    void delete(QuestionModel questionModel);

    @Query("DELETE FROM questions_options")
    void deleteAllQuestions();

    @Query("SELECT * FROM questions_options ORDER BY questionNo ASC")
    List<QuestionModel> getAllQuestionsAndOptions();
}
