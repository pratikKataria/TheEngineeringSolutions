package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tes.theengineeringsolutions.Adapters.RecyclerViewAdapter;
import com.tes.theengineeringsolutions.Models.QuizContract;
import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {

    List<QuizContract> list;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    public InboxFragment() {
        // Required empty public constructor
    }

    private void init_fields(View view) {
        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
//        populateList();
        init_recyclerView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        init_fields(view);
        return view;
    }

    private void init_recyclerView() {
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), list, 1);
        final  LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

//    private void init_recyclerView() {
//        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), testList, 1);
//
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setOrientation(RecyclerView.VERTICAL);
//        testRecyclerView.setLayoutManager(layoutManager);
//        testRecyclerView.setAdapter(recyclerViewAdapter);
//    }

    private void populateList() {
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
        list.add(new QuizContract());
    }

}
