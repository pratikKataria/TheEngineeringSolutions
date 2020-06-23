package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.tes.theengineeringsolutions.Adapters.RecyclerViewAdapter;
import com.tes.theengineeringsolutions.Models.LocalTestDatabase;
import com.tes.theengineeringsolutions.Models.QuizContract;
import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;
import java.util.List;

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
    private NestedScrollView nestedScrollView;

    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;


    public TestFragment() {
        // Required empty public constructor
    }


    private void init_fields(View view) {
        testRecyclerView = view.findViewById(R.id.fragTest_rv);
        testList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        reloadBtn = view.findViewById(R.id.fragmentTest_reload_btn);
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


        init_fields(view);

        init_recyclerView();


        LocalTestDatabase.deleteAll(LocalTestDatabase.class);

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
        Toast.makeText(getActivity(), "reloading...", Toast.LENGTH_SHORT).show();
    }


    private void init_recyclerView() {
        if (getActivity() == null) return;
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), testList, 1);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        testRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        testRecyclerView.setAdapter(recyclerViewAdapter);
    }


    private void populateTestList() {
        Query firstQuery = firebaseFirestore.collection("Admin").orderBy("created", Query.Direction.DESCENDING);
        firstQuery.limit(30);
        listenerRegistration = firstQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    QuizContract quizContract = doc.getDocument().toObject(QuizContract.class);
                    if (quizContract.getSubject_code() != null) {
                        testList.add(quizContract);
                        if (testList.size() == 1) {
                            recyclerViewAdapter.notifyDataSetChanged();
                        }else {
                            recyclerViewAdapter.notifyItemInserted(testList.indexOf(quizContract));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TestFragment.class.getName(), "onStop");
        if (listenerRegistration != null) {
            listenerRegistration.remove();
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
