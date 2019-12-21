package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hadiidbouk.charts.BarData;
import com.hadiidbouk.charts.ChartProgressBar;
import com.tes.theengineeringsolutions.Adapters.RecyclerViewAdapter;
import com.tes.theengineeringsolutions.Adapters.SpacesItemDecoration;
import com.tes.theengineeringsolutions.Models.QuizContract;
import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultFragment extends Fragment {

    public static final String TAG = "RESULT FRAGMENT";

    private RecyclerView resultRecyclerView;
    private List<QuizContract> resultList;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ChartProgressBar chartProgressBar;

    public ResultFragment() {
        // Required empty public constructor
    }

    public void initFields(View view) {
        resultRecyclerView = view.findViewById(R.id.result_recycler_view);
        chartProgressBar = view.findViewById(R.id.ChartProgressBar);
        resultList = new ArrayList<>();
        chartView();
        populateList();
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), resultList, 2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_result, container, false);

        initFields(view);
        recyclerViewAdapter.notifyDataSetChanged();

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(23);
        resultRecyclerView.addItemDecoration(spacesItemDecoration);
        resultRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        resultRecyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }

    private void populateList() {
        FirebaseFirestore.getInstance().collection("Results").addSnapshotListener((queryDocumentSnapshots, e) -> {
            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                Map<String, Object> quizContract = doc.getDocument().getData();
                for (Object object : quizContract.keySet()) {
                    Map<String, String> subData = (Map<String, String>) quizContract.get(object);
                    Log.e("RESULT FRAGMENT", subData.get("subject") + "/n" + subData.get("percentage"));
                    QuizContract quizContract1 = new QuizContract(
                            subData.get("subject") + "",
                            subData.get("subject_code") + "",
                            subData.get("date") + "",
                            subData.get("result") + "",
                            subData.get("percentage") + "",
                            subData.get("question_correct") + "",
                            subData.get("questions_incorrect") + "",
                            subData.get("total_questions") + "",
                            subData.get("color") + "",
                            subData.get("badge") + "");
                    resultList.add(quizContract1);
                    recyclerViewAdapter.notifyDataSetChanged();
                    Log.e(TAG, "list size " + resultList.size());
                }
            }
        });
    }

    private void chartView() {
        ArrayList<BarData> dataList = new ArrayList<>();

        BarData data = new BarData("Sep", 3.4f, "3 passed");
        dataList.add(data);

        data = new BarData("Oct", 8f, "8 passed");
        dataList.add(data);

        data = new BarData("Nov", 1.8f, "1 passed");
        dataList.add(data);

        data = new BarData("Dec", 7.3f, "7 passed");
        dataList.add(data);

        data = new BarData("Jan", 6.2f, "6 passed");
        dataList.add(data);

        data = new BarData("Feb", 3.3f, "3 passed");
        dataList.add(data);

        chartProgressBar.setDataList(dataList);
        chartProgressBar.build();
    }


}
