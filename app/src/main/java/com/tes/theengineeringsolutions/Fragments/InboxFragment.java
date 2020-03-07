package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tes.theengineeringsolutions.Adapters.InboxRecyclerViewAdapter;
import com.tes.theengineeringsolutions.Models.InboxModel;
import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {

    private RecyclerView inboxRecyclerView;
    private InboxRecyclerViewAdapter inboxRecyclerViewAdapter;
    InboxRecyclerViewAdapter adapter;
    private ArrayList<InboxModel> inboxList;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference inboxRef = firebaseFirestore.collection("InboxPost");


    public InboxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Pass your adapter interface in the constructor
    }

    // This is the interface declared inside your adapter.
    InboxRecyclerViewAdapter.InfoAdapterInterface adapterInterface = new InboxRecyclerViewAdapter.InfoAdapterInterface() {
        @Override
        public void OnItemClicked(int item_id) {
            inboxList.remove(item_id);
            inboxList.clear();
            adapter.notifyDataSetChanged();
            populateInboxList();
            // Do whatever you wants to do with this data that is coming from your adapter
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        inboxList = new ArrayList<>();

//        init_recyclerView(view);
        setUpRecyclerView(view);
        populateInboxList();

        return view;
    }

//    private void init_recyclerView(View view) {
//        inboxRecyclerView = view.findViewById(R.id.recyclerView);
////        inboxRecyclerViewAdapter = new InboxRecyclerViewAdapter(getContext(), inboxList);
//        adapter = new InboxRecyclerViewAdapter(getContext(), inboxList, adapterInterface );
//
//        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
//        inboxRecyclerView.setLayoutManager(verticalLayoutManager);
//        inboxRecyclerView.setAdapter(adapter);
//    }

    private void setUpRecyclerView(View view) {
        Query query = inboxRef.orderBy("created", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<InboxModel> options = new FirestoreRecyclerOptions.Builder<InboxModel>().setQuery(query, InboxModel.class).build();

        adapter = new InboxRecyclerViewAdapter(options, getContext());

        inboxRecyclerView = view.findViewById(R.id.recyclerView);
        inboxRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        inboxRecyclerView.setLayoutManager(layoutManager);
        inboxRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void populateInboxList() {
        Query firstQuery = firebaseFirestore.collection("InboxPost").orderBy("created", Query.Direction.DESCENDING);


        firstQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    InboxModel inboxModel = documentChange.getDocument().toObject(InboxModel.class);
                    if (inboxModel != null && inboxModel.getCreated() != null)
                        inboxList.add(inboxModel);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }



}
