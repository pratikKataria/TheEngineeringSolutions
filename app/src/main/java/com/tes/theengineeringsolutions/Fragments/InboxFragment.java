package com.tes.theengineeringsolutions.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tes.theengineeringsolutions.Adapters.InboxRecyclerViewAdapter;
import com.tes.theengineeringsolutions.Models.InboxModel;
import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {

    private RecyclerView inboxRecyclerView;
    private InboxRecyclerViewAdapter inboxRecyclerViewAdapter;
    private ArrayList<InboxModel> inboxList;

    public InboxFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        inboxList = new ArrayList<>();

        init_recyclerView(view);
        populateInboxList();

        return view;
    }

    private void init_recyclerView(View view) {
        inboxRecyclerView = view.findViewById(R.id.recyclerView);
        inboxRecyclerViewAdapter = new InboxRecyclerViewAdapter(getContext(), inboxList);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        inboxRecyclerView.setLayoutManager(verticalLayoutManager);
        inboxRecyclerView.setAdapter(inboxRecyclerViewAdapter);
    }

    private void populateInboxList() {
        Date date = new Date();

        InboxModel inboxModel = new InboxModel("Holiday", "coaching will remain closed form 08th oc,\n" +
                "to 15th oc, on account of Durga puja, Dasmi \n" +
                "& Laxmi Puja. Class will resume on 16th oc 2019", date);
        inboxList.add(inboxModel);

        inboxModel = new InboxModel("Holiday", "coaching will remain closed form 08th oc,\n" +
                "to 15th oc, on account of Durga puja, Dasmi \n" +
                "& Laxmi Puja. Class will resume on 16th oc 2019", date);
        inboxList.add(inboxModel);

        inboxModel = new InboxModel("Holiday", "coaching will remain closed form 08th oc,\n" +
                "to 15th oc, on account of Durga puja, Dasmi \n" +
                "& Laxmi Puja. Class will resume on 16th oc 2019", date);
        inboxList.add(inboxModel);

        inboxModel = new InboxModel("Holiday", "coaching will remain closed form 08th oc,\n" +
                "to 15th oc, on account of Durga puja, Dasmi \n" +
                "& Laxmi Puja. Class will resume on 16th oc 2019", date);
        inboxList.add(inboxModel);

        inboxModel = new InboxModel("Holiday", "coaching will remain closed form 08th oc,\n" +
                "to 15th oc, on account of Durga puja, Dasmi \n" +
                "& Laxmi Puja. Class will resume on 16th oc 2019", date);
        inboxList.add(inboxModel);

        inboxModel = new InboxModel("Holiday", "coaching will remain closed form 08th oc,\n" +
                "to 15th oc, on account of Durga puja, Dasmi \n" +
                "& Laxmi Puja. Class will resume on 16th oc 2019", date);
        inboxList.add(inboxModel);

        inboxModel = new InboxModel("Holiday", "coaching will remain closed form 08th oc,\n" +
                "to 15th oc, on account of Durga puja, Dasmi \n" +
                "& Laxmi Puja. Class will resume on 16th oc 2019", date);
        inboxList.add(inboxModel);

        inboxModel = new InboxModel("Holiday", "coaching will remain closed form 08th oc,\n" +
                "to 15th oc, on account of Durga puja, Dasmi \n" +
                "& Laxmi Puja. Class will resume on 16th oc 2019", date);
        inboxList.add(inboxModel);

        inboxRecyclerViewAdapter.notifyDataSetChanged();
    }

}
