package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
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

    InboxRecyclerViewAdapter adapter;
    private RecyclerView inboxRecyclerView;
    private LinearLayout linearLayout;
    private InboxRecyclerViewAdapter inboxRecyclerViewAdapter;
    private ArrayList<InboxModel> inboxList;

    // This is the interface declared inside your adapter.
    InboxRecyclerViewAdapter.InboxAdapterListener listener = new InboxRecyclerViewAdapter.InboxAdapterListener() {
        @Override
        public void onEmptyStateListener(boolean isEmpty) {
            if (isEmpty) {
                inboxRecyclerView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                inboxRecyclerView.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
            }
        }
    };

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference inboxRef;

    public InboxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Pass your adapter interface in the constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        linearLayout = view.findViewById(R.id.fragment_inbox_ll_empty_view);
        inboxRecyclerView = view.findViewById(R.id.recyclerView);

        firebaseFirestore = FirebaseFirestore.getInstance();
        inboxRef = firebaseFirestore.collection("InboxPost");
        inboxList = new ArrayList<>();

//        init_recyclerView(view);
        setUpRecyclerView(view);
//        populateInboxList();


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


        adapter = new InboxRecyclerViewAdapter(options, getContext() , listener);

        FirestoreRecyclerAdapter<InboxModel, InboxRecyclerViewAdapter.InboxViewHolder> fh =
                new FirestoreRecyclerAdapter<InboxModel, InboxRecyclerViewAdapter.InboxViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull InboxRecyclerViewAdapter.InboxViewHolder holder, int position, @NonNull InboxModel model) {

                    }

                    @NonNull
                    @Override
                    public InboxRecyclerViewAdapter.InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return null;
                    }
                };



//        Toast.makeText(getContext(), "items " + adapter.getItemCount() + "snap sort size " + options.getSnapshots().size(), Toast.LENGTH_SHORT).show();
//
//        if (adapter.getItemCount() == 0) {
//            inboxRecyclerView.setVisibility(View.GONE);
//            linearLayout.setVisibility(View.VISIBLE);
//        } else {
//            inboxRecyclerView.setVisibility(View.VISIBLE);
//            linearLayout.setVisibility(View.GONE);
//        }



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

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
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
