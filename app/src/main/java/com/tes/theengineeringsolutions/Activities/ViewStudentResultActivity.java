package com.tes.theengineeringsolutions.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.tes.theengineeringsolutions.R;

public class ViewStudentResultActivity extends AppCompatActivity {

    private TextView textViewRestult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_result);

        textViewRestult = findViewById(R.id.view_student_result_activity_tv_data);

    }
}
