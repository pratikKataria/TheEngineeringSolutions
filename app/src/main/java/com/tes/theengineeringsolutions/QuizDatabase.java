package com.tes.theengineeringsolutions;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tes.theengineeringsolutions.Models.QuestionModel;

@Database(entities = QuestionModel.class, version = 2)
public abstract class QuizDatabase extends RoomDatabase {

    private static QuizDatabase instance;

    public abstract LocalTestDatabaseDoa testDatabaseDoa();

    public static synchronized QuizDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    QuizDatabase.class, "questions_options"
            ).fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
