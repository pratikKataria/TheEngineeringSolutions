package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tes.theengineeringsolutions.Adapters.RecyclerViewAdapter;
import com.tes.theengineeringsolutions.Models.LocalTestDatabase;
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
    private Chip reloadBtn;
    private TextView textView;
    private NestedScrollView nestedScrollView;

    private FirebaseFirestore firebaseFirestore;

    public TestFragment() {
        // Required empty public constructor
    }


    private void init_fields(View view) {
        testRecyclerView = view.findViewById(R.id.fragTest_rv);
        testList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        reloadBtn = view.findViewById(R.id.fragmentTest_reload_btn);
        textView = view.findViewById(R.id.textviewFORTEST);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);

        populateTestList();
        new Handler().postDelayed(() -> {
            if (testList.size() == 0) {
                hideList();
            }
        }, 2000);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        LocalTestDatabase.deleteAll(LocalTestDatabase.class);

        init_fields(view);
        init_recyclerView();

        recyclerViewAdapter.notifyDataSetChanged();


        reloadBtn.setOnClickListener(v -> {
            reload();
            showList();
        });
        return view;
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
                   if (snapshot != null) {
                        Map<String, Object> data = snapshot.getData();
                        Map<String, Boolean> rootMap = (Map<String, Boolean>) data.get("test_completed");
                        firebaseFirestore.collection("Admin").addSnapshotListener((queryDocumentSnapshots, e) -> {
                            if (queryDocumentSnapshots != null) {
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
                            }
                            if (testList.size() == 0)
                                new Handler().postDelayed(this::hideList, 1500);
                            else
                                showList();
                        });
                    }
                }
            });
        }
    }

    private void hideList() {
        nestedScrollView.setVisibility(View.VISIBLE);
        testRecyclerView.setVisibility(View.GONE);
    }

    private void showList() {
        nestedScrollView.setVisibility(View.GONE);
        testRecyclerView.setVisibility(View.VISIBLE);
    }
}
