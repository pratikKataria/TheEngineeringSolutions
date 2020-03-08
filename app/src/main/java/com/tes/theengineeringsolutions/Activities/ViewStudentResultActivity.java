package com.tes.theengineeringsolutions.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tes.theengineeringsolutions.R;

import java.util.List;
import java.util.Map;

public class ViewStudentResultActivity extends AppCompatActivity {

    private TextView textViewRestult;

    private String bigData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_result);

        textViewRestult = findViewById(R.id.view_student_result_activity_tv_data);

        String uid = getIntent().getStringExtra("USER_ID") != null ? getIntent().getStringExtra("USER_ID") : "-1";

        textViewRestult.setText(uid);

        loadData(uid);
    }

    private void loadData(String uid) {
        if (uid != null) {
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Results").document(uid);
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        Map<String, Object> map = snapshot.getData();
                        Log.e("View Student Result activity", map.toString());
                    }
                }
            });
        }
    }
}
