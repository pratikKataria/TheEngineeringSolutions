package com.tes.theengineeringsolutions.Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tes.theengineeringsolutions.R;

import java.util.Map;

public class ViewStudentResultActivity extends AppCompatActivity {

    private TextView textViewRestult;
    private TextView textViewUserName;
    private TextView textViewUserId;

    private String bigData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_result);

        textViewRestult = findViewById(R.id.view_student_result_activity_tv_data);

        String uid = getIntent().getStringExtra("USER_ID") != null ? getIntent().getStringExtra("USER_ID") : "-1";
        String uName = getIntent().getStringExtra("USER_NAME") != null ? getIntent().getStringExtra("USER_ID") : "-1";

        textViewUserName.setText(uName);
        textViewUserId.setText(uid);
        loadData(uid);
    }

    private void loadData(String uid) {
        if (uid != null) {
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Results").document(uid);
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        Map<String, Object> rootMap = snapshot.getData();
                        if (rootMap != null) {
                            StringBuilder builder = new StringBuilder();
                            for (String keys : rootMap.keySet()) {
                                builder.append("\t+ "+(keys).toUpperCase() +"\n");
                                Map<String, String> subMap = (Map<String, String>) rootMap.get(keys);
                                if (subMap != null) {
                                    builder.append("\t |- subject: \t" + subMap.get("subject") +"\n");
                                    builder.append("\t |- subject code: \t" + subMap.get("subject_code") + "\n");
                                    builder.append("\t |- question correct: \t" + subMap.get("question_correct") + "\n");
                                    builder.append("\t |- question incorrect: \t" + subMap.get("questions_incorrect") + "\n");
                                    builder.append("\t |- question unanswered: \t" + subMap.get("questions_unanswered") + "\n");
                                    builder.append("\t |- marks obtained: \t" + subMap.get("question_correct") + "/" + subMap.get("total_questions"));
                                    builder.append("\n--------------------------------\n");
                                }
                            }
                            textViewRestult.setText(builder.toString());
                        }
                    }
                }
            });
        }
    }
}
