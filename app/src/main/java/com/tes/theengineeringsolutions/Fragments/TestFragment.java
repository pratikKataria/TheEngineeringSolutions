package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.tes.theengineeringsolutions.Adapters.RecyclerViewAdapter;
import com.tes.theengineeringsolutions.Models.QuizContract;
import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * created by pratik katariya
 * updated on 5/12/2019
 */
public class TestFragment extends Fragment {

    private RecyclerView testRecyclerView;
    private List<QuizContract> testList;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Chip reloadBtn;
    private TextView textView;
//    private SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseFirestore firebaseFirestore;

    public TestFragment() {
        // Required empty public constructor
    }


    private void initializeFields(View view) {
        testRecyclerView = view.findViewById(R.id.fragTest_rv);
        testList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        reloadBtn = view.findViewById(R.id.fragmentTest_reload_btn);
        textView = view.findViewById(R.id.textviewFORTEST);
//        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);

        populateTestList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        initializeFields(view);
        init_recyclerView();
        getProgress();
        recyclerViewAdapter.notifyDataSetChanged();

        reloadBtn.setOnClickListener(v -> {
            reload();
        });
        return view;
    }


    private void getProgress() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getUid());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> data = snapshot.getData();
                    if (data != null && data.containsKey("test_progress")) {
                        Map<String, Object> month1 = (Map<String, Object>) data.get("test_progress");
                        String temp = "";
                        for (int i = -6; i < 1; i++) {
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.MONTH, i);
                            int month = cal.get(Calendar.MONTH);
                            int year = cal.get(Calendar.YEAR);
                            String stringDate = (month + 1) + "-" + year;
                            if (month1.containsKey(stringDate)) {
                                Log.e("TESTFRAGMENT" , "DATE " + stringDate +" pass " + month1.get(stringDate) + " i " + i);
                                temp += month1.get(stringDate);
                            }
                        }
                        textView.setText(temp);
                    }
                }
            }
        });
    }
    private void reload() {
        testList.clear();
        recyclerViewAdapter.notifyDataSetChanged();
        new Handler().postDelayed(() -> {
            populateTestList();
            recyclerViewAdapter.notifyDataSetChanged();
        }, 1500);
        Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
    }

    private void init_recyclerView() {
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), testList, 1);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        testRecyclerView.setLayoutManager(layoutManager);
        testRecyclerView.setAdapter(recyclerViewAdapter);
    }

    private void populateTestList() {
        if (FirebaseAuth.getInstance().getUid() != null) {
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getUid());
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    assert snapshot != null;
                    if (snapshot.exists()) {
                        Map<String, Object> data = snapshot.getData();
                        Map<String, Boolean> rootMap = (Map<String, Boolean>) data.get("test_completed");
                        firebaseFirestore.collection("Admin").addSnapshotListener((queryDocumentSnapshots, e) -> {
                            assert queryDocumentSnapshots != null;
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                QuizContract quizContract = doc.getDocument().toObject(QuizContract.class);
                                if (rootMap != null) {
                                    if (rootMap.containsKey(quizContract.getSubject_code())) {
                                        if (!rootMap.get(quizContract.getSubject_code())) {
                                            testList.add(quizContract);
                                        }
                                    }
                                }
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            });
        }
    }
}
