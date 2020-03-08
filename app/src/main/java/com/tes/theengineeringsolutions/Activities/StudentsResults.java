package com.tes.theengineeringsolutions.Activities;

import android.app.ListActivity;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tes.theengineeringsolutions.Adapters.ListCustomAdapter;
import com.tes.theengineeringsolutions.Models.UserDataModel;
import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;

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
            Toast.makeText(StudentsResults.this, "items clicked " + dataModel.getName(), Toast.LENGTH_SHORT).show();
        });

    }

    private void setUpListView() {
        listView = findViewById(R.id.students_result_activity_lv_results);

        listCustomAdapter = new ListCustomAdapter(this, R.layout.card_view_list_items, dataModels);
        listView.setAdapter(listCustomAdapter);
    }

    private void populateList() {
        dataModels.add(new UserDataModel("pratik ", "@gmail.com"));
        dataModels.add(new UserDataModel("pratik 2", "@gmail.com"));
        dataModels.add(new UserDataModel("pratik 3", "@gmail.com"));
        dataModels.add(new UserDataModel("pratik 4", "@gmail.com"));
        dataModels.add(new UserDataModel("pratik 5", "@gmail.com"));
        dataModels.add(new UserDataModel("pratik 6", "@gmail.com"));
        dataModels.add(new UserDataModel("pratik 7", "@gmail.com"));
    }
}
