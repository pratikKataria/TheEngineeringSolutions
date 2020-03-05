package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        inti_recyclerView();

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
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Map<String, Object> header = documentSnapshot.getData();
                        if (header != null) {
//                            Log.e(TAG, "header" + header.keySet().toString());
                            for (Object object : header.keySet()) {
                                Map<String, String> subData = (Map<String, String>) header.get(object);
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
                    } else {
                        Toast.makeText(getActivity(), "you do not given any test", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void getProgress() {
        if (FirebaseAuth.getInstance().getUid() != null) {
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getUid());
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        Map<String, Object> data = snapshot.getData();
                        if (data != null && data.containsKey("test_progress")) {
                            Map<String, Object> month1 = (Map<String, Object>) data.get("test_progress");


                            for (int i = -6; i < 1; i++) {
                                Calendar cal = Calendar.getInstance();
                                cal.add(Calendar.MONTH, i);
                                int month = cal.get(Calendar.MONTH);
                                int year = cal.get(Calendar.YEAR);
                                String stringDate;

                                if (month+1 < 10) {
                                stringDate = 0 + "" +(month + 1) + "-" + year;
                                } else  stringDate = (month + 1) + "-" + year;

                                Log.e("RESULT FRAGMENT", stringDate);
                                if (month1.containsKey(stringDate)) {

                                    Log.e("RRRRRRRRRRRRRRRRRRRRREEEEEEEEEEEESSSSSSSSSSSSSSSSUUUUUUUUUUULLLLLLLLLLTTTTTTTT", month1.get(stringDate) +"" );


                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
                                    float val = (float) (((long) month1.get(stringDate)));

                                    BarData barData;
                                    if (val > 30) {
                                        barData = new BarData(simpleDateFormat.format(cal.getTime()) + " " + year, 49, month1.get(stringDate) + " passed");
                                    } else {
                                        barData = new BarData(simpleDateFormat.format(cal.getTime()) + " " + year, val, month1.get(stringDate) + " passed");
                                    }
                                    progressList.add(barData);
                                }
                            }

                            chartView();
                        }
                    }
                }
            });
        }
    }

    private void inti_recyclerView() {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(23);
        resultRecyclerView.addItemDecoration(spacesItemDecoration);
        resultRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        resultRecyclerView.setAdapter(recyclerViewAdapter);
    }

    private void chartView() {
        if (progressList.size() > 0) {
            chartProgressBar.setDataList(progressList);
            chartProgressBar.build();
        } else {
            ArrayList<BarData> emptyProgress = new ArrayList<>();
            for (int i = -6; i < 1; i++) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, i);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                String stringDate = (month + 1) + "-" + year;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
                BarData emptyBar = new BarData(simpleDateFormat.format(cal.getTime()) + " " + year, 1, "0 passed");
                emptyProgress.add(emptyBar);
            }
            chartProgressBar.setDataList(emptyProgress);
            chartProgressBar.build();
        }
    }

}
