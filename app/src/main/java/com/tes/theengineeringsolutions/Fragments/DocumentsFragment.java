package com.tes.theengineeringsolutions.Fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tes.theengineeringsolutions.Adapters.DocumentRecyclerViewAdapter;
import com.tes.theengineeringsolutions.Models.NotesModel;
import com.tes.theengineeringsolutions.R;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DocumentRecyclerViewAdapter documentRecyclerViewAdapter;
    private ArrayList<NotesModel> notesList;

    private Uri fileUri;


    public DocumentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_documents, container, false);

        notesList = new ArrayList<>();

        init_reyclerView(view);
        populateList();

        return view;
    }

    private void init_reyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        documentRecyclerViewAdapter = new DocumentRecyclerViewAdapter(getActivity(), notesList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(documentRecyclerViewAdapter);
    }

    private void populateList() {
        Date n = new Date();
        NotesModel notesModel = new NotesModel("Physics", "uri" ,  n);
        notesList.add(notesModel);

        notesModel = new NotesModel("Chemistry", "uri" ,  n);
        notesList.add(notesModel);

        notesModel = new NotesModel("Maths", "uri" ,  n);
        notesList.add(notesModel);

        notesModel = new NotesModel("Biology", "uri" ,  n);
        notesList.add(notesModel);

        notesModel = new NotesModel("Electrical", "uri" ,  n);
        notesList.add(notesModel);

        documentRecyclerViewAdapter.notifyDataSetChanged();
    }
}
