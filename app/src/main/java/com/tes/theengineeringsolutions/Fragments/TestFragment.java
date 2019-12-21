package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        setProgress();
        recyclerViewAdapter.notifyDataSetChanged();

        reloadBtn.setOnClickListener(v -> {
            reload();
        });
        return view;
    }

    private void setProgress() {
        Map<String, Object> header = new HashMap<>();
        Map<String, Integer> data = new HashMap<>();
        int countpass = 0;
        String stringDate = "Sat-21-07-2019";
        DocumentReference reference = FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getUid());
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    Map<String, Object> data1 = documentSnapshot.getData();
                    if (data1 != null) {
                        if (data1.containsKey("test_progress")) {
                            if (((Map<String, Object>) data1.get("test_progress")).containsKey(stringDate.substring(7))) {
                                long number = (Long) ((Map<String, Object>) data1.get("test_progress")).get(stringDate.substring(7));
                                data.put(stringDate.substring(7), ((int) number + 1));
                                header.put("test_progress", data);
                                reference.set(header, SetOptions.merge());
                                Toast.makeText(getActivity(), "progress upgraded", Toast.LENGTH_SHORT).show();
                            }else {
                                data.put(stringDate.substring(7), 0);
                                header.put("test_progress", data);
                                reference.set(header, SetOptions.merge());
                                Toast.makeText(getActivity(), "progress set", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            data.put(stringDate.substring(7), 0);
                            header.put("test_progress", data);
                            reference.set(header, SetOptions.merge());
                            Toast.makeText(getActivity(), "progress set", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                String e = task.getException().getMessage();
                Log.e("TESTFRAGMENT", e);
                Toast.makeText(getActivity(), "failed to set progress", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TESTFRAGMENT", e.getMessage());
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
