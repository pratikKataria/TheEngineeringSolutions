package com.tes.theengineeringsolutions.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hadiidbouk.charts.BarData;
import com.hadiidbouk.charts.ChartProgressBar;
import com.tes.theengineeringsolutions.Adapters.RecyclerViewAdapter;
import com.tes.theengineeringsolutions.Adapters.SpacesItemDecoration;
import com.tes.theengineeringsolutions.Models.QuizContract;
import com.tes.theengineeringsolutions.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultFragment extends Fragment {


    private RecyclerView resultRecyclerView;
    private List<QuizContract> resultList;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ChartProgressBar chartProgressBar;
    private ArrayList<BarData> progressList;
    private ImageButton reloadBtn;


    public ResultFragment() {
        // Required empty public constructor
    }

    public void initFields(View view) {
        resultRecyclerView = view.findViewById(R.id.result_recycler_view);
        chartProgressBar = view.findViewById(R.id.ChartProgressBar);
        reloadBtn = view.findViewById(R.id.reload_bar_btn);

        resultList = new ArrayList<>();
        progressList = new ArrayList<>();
        getProgress();
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
        init_recyclerView();

        reloadBtn.setOnClickListener(v -> {
            progressList.clear();
            getProgress();
            chartView();
        });


        return view;
    }

    private void populateList() {
        if (FirebaseAuth.getInstance().getUid() != null) {
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Results").document(FirebaseAuth.getInstance().getUid());
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot != null && documentSnapshot.exists() && documentSnapshot.getData() != null) {
                        Map<String, Object> header = documentSnapshot.getData();
                        for (Object object : header.keySet()) {
                            Map<String, String> subData = (Map<String, String>) header.get(object);

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
                        }
                    } else {
                        Toast.makeText(getActivity(), "you do not given any test", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void getProgress() {
        if (FirebaseAuth.getInstance().getUid() != null) {
            DocumentReference testProgressCollectionReference = FirebaseFirestore.getInstance().collection("TestProgress").document(FirebaseAuth.getInstance().getUid());
            testProgressCollectionReference.get().addOnSuccessListener(documentSnapshot -> {
                Map<String, Object> data = documentSnapshot.getData();
                if (data != null) {
                    Log.e("Result Fragment ", data + " ");
                    for (int month = -6; month < 1; month++) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.add(Calendar.MONTH, month);
                        String pastDate = new SimpleDateFormat("MM'-'YYYY").format(calendar.getTime());

                        if (data.containsKey(pastDate)) {
                            float val = (float) (((long) data.get(pastDate)));

                            BarData barData;
                            if (val > 30) {
                                barData = new BarData(new SimpleDateFormat("MMM - YYYY").format(calendar.getTime()), 49, data.get(pastDate) + " passed");
                            } else {
                                barData = new BarData(new SimpleDateFormat("MMM - YYYY").format(calendar.getTime()), val, data.get(pastDate) + " passed");
                            }
                            progressList.add(barData);
                        }
                    }
                    chartView();
                }
            });
        }
    }

    private void init_recyclerView() {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(23);
        resultRecyclerView.addItemDecoration(spacesItemDecoration);
        resultRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        resultRecyclerView.setAdapter(recyclerViewAdapter);
    }

    @SuppressLint("SimpleDateFormat")
    private void chartView() {
        if (!progressList.isEmpty()) {
            chartProgressBar.setDataList(progressList);
            chartProgressBar.build();
        } else {
            ArrayList<BarData> emptyProgress = new ArrayList<>();
            for (int i = -6; i < 1; i++) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, i);
                BarData emptyBar = new BarData(new SimpleDateFormat("MMM'-'YYYY").format(cal.getTime()), 1, "0 passed");
                emptyProgress.add(emptyBar);
            }
            chartProgressBar.setDataList(emptyProgress);
            chartProgressBar.build();
        }
    }

}
