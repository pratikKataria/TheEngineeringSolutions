package com.tes.theengineeringsolutions.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tes.theengineeringsolutions.Adapters.ListCustomAdapter;
import com.tes.theengineeringsolutions.Models.UserDataModel;
import com.tes.theengineeringsolutions.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.content.Intent;

public class StudentsResults extends AppCompatActivity {

    ArrayList<UserDataModel> dataModels;
    private ListView listView;
    private ListCustomAdapter listCustomAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_results);

        dataModels = new ArrayList<>();

        populateList();

        setUpListView();


        listView.setOnItemClickListener((parent, view, position, id) -> {
            UserDataModel dataModel = dataModels.get(position);
            Intent intent = new Intent(this, ViewStudentResultActivity.class);
            intent.putExtra("USER_ID", dataModels.get(position).getUserId());
            Toast.makeText(StudentsResults.this, "getting information .... " + dataModel.getName(), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> {
                startActivity(intent);
            },800);
        });

    }

    private void setUpListView() {
        listView = findViewById(R.id.students_result_activity_lv_results);

        listCustomAdapter = new ListCustomAdapter(this, R.layout.card_view_list_items, dataModels);
        listView.setAdapter(listCustomAdapter);
    }

    private void populateList() {
//        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User").document();
//
//        documentReference.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot snapshot = task.getResult();
//                if (snapshot != null && snapshot.exists()) {
//                    Map<String, Object> root = snapshot.getData();
//                    if (root.containsKey("user_info")) {
//                        Map<String, String> data = (Map<String, String>) root.get("user_info");
//                        dataModels.add(new UserDataModel(data.get("user_name"), data.get("email_address")));
//                        listCustomAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        });

        FirebaseFirestore.getInstance().collection("User").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> root = documentSnapshot.getData();
                        if (root != null && root.containsKey("user_info")) {
                            Map<String, String> data = (Map<String, String>) root.get("user_info");
                            if (data != null) {
                                dataModels.add(new UserDataModel("Name : "+data.get("user_name"), "email : "+data.get("email_address"), "password : " + data.get("password"), data.get("user_id")));
                                listCustomAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });

    }
}
