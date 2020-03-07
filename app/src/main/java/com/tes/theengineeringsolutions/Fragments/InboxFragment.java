package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tes.theengineeringsolutions.Adapters.InboxRecyclerViewAdapter;
import com.tes.theengineeringsolutions.Models.InboxModel;
import com.tes.theengineeringsolutions.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {

    private InboxRecyclerViewAdapter adapter;
    private RecyclerView inboxRecyclerView;
    private LinearLayout emptyLinearLayout;
    private LinearLayout topHeaderLinearLayout;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference inboxRef;

    private InboxRecyclerViewAdapter.InboxAdapterListener listener = new InboxRecyclerViewAdapter.InboxAdapterListener() {
        @Override
        public void onEmptyStateListener(boolean isEmpty) {
            if (isEmpty) {
                inboxRecyclerView.setVisibility(View.GONE);
                topHeaderLinearLayout.setVisibility(View.GONE);
                emptyLinearLayout.setVisibility(View.VISIBLE);
            } else {
                inboxRecyclerView.setVisibility(View.VISIBLE);
                topHeaderLinearLayout.setVisibility(View.VISIBLE);
                emptyLinearLayout.setVisibility(View.GONE);
            }
        }
    };

    public InboxFragment() {
        // Required empty public constructor
    }

    private void init_fields(View view) {
        emptyLinearLayout = view.findViewById(R.id.fragment_inbox_ll_empty_view);
        inboxRecyclerView = view.findViewById(R.id.recyclerView);
        topHeaderLinearLayout = view.findViewById(R.id.fragment_inbox_ll_top_header);

        firebaseFirestore = FirebaseFirestore.getInstance();
        inboxRef = firebaseFirestore.collection("InboxPost");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        init_fields(view);

        setUpRecyclerView();

        return view;
    }

    private void setUpRecyclerView() {
        Query query = inboxRef.orderBy("created", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<InboxModel> options = new FirestoreRecyclerOptions.Builder<InboxModel>().setQuery(query, InboxModel.class).build();

        adapter = new InboxRecyclerViewAdapter(options, getContext(), listener);

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
}
