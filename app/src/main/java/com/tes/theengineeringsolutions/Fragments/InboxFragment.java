package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tes.theengineeringsolutions.Adapters.InboxRecyclerViewAdapter;
import com.tes.theengineeringsolutions.Models.InboxModel;
import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {

    private RecyclerView inboxRecyclerView;
    private InboxRecyclerViewAdapter inboxRecyclerViewAdapter;
    private ArrayList<InboxModel> inboxList;

    private FirebaseFirestore firebaseFirestore;

    public InboxFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        inboxList = new ArrayList<>();

        init_recyclerView(view);
        populateInboxList();

        return view;
    }

    private void init_recyclerView(View view) {
        inboxRecyclerView = view.findViewById(R.id.recyclerView);
        inboxRecyclerViewAdapter = new InboxRecyclerViewAdapter(getContext(), inboxList);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        inboxRecyclerView.setLayoutManager(verticalLayoutManager);
        inboxRecyclerView.setAdapter(inboxRecyclerViewAdapter);
    }

    private void populateInboxList() {

        Query firstQuery = firebaseFirestore.collection("InboxPost").orderBy("created", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    InboxModel inboxModel = documentChange.getDocument().toObject(InboxModel.class);
                    if (inboxModel != null && inboxModel.getCreated() != null)
                        inboxList.add(inboxModel);
                    inboxRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
