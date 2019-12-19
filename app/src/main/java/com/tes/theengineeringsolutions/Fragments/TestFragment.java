package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tes.theengineeringsolutions.Adapters.RecyclerViewAdapter;
import com.tes.theengineeringsolutions.Models.QuizContract;
import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;
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

    private FirebaseFirestore firebaseFirestore;

    public TestFragment() {
        // Required empty public constructor
    }


    private void initializeFields(View view) {
        testRecyclerView = view.findViewById(R.id.fragTest_rv);
        testList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();

        populateTestList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        initializeFields(view);
        init_recyclerView();
        recyclerViewAdapter.notifyDataSetChanged();

        return view;
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
                DocumentSnapshot snapshot = task.getResult();
                assert snapshot != null;
                if (snapshot.exists()) {
                    Map<String, Object> data = snapshot.getData();
                    Map<String, Boolean> rootMap = (Map<String, Boolean>) data.get("test_completed");

                    firebaseFirestore.collection("Admin").addSnapshotListener((queryDocumentSnapshots, e) -> {
                        assert queryDocumentSnapshots != null;
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            QuizContract quizContract = doc.getDocument().toObject(QuizContract.class);
                            if (rootMap.containsKey(quizContract.getSubject_code())) {
                                if (!rootMap.get(quizContract.getSubject_code())) {
                                    testList.add(quizContract);
                                }
                            }
                            recyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        }
    }
}
