package com.tes.theengineeringsolutions.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tes.theengineeringsolutions.Activities.UploadTestFile;
import com.tes.theengineeringsolutions.Adapters.InboxRecyclerViewAdapter;
import com.tes.theengineeringsolutions.CustomViewPager.SectionsPagerAdapter;
import com.tes.theengineeringsolutions.Models.InboxModel;
import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    //ui for tab layout
    private TabLayout mNavigationTabStrip;
    //tab view holder
    private ViewPager viewPager;

    //fab for uploading test file
    private FloatingActionButton mUploadTest;
    //fab for posting feed
    private FloatingActionButton mPost;
    //fab for uploading document
    private FloatingActionButton mUploadDoc;

    private TextView textViewUsername;

    public HomeFragment() {
        // Required empty public constructor
    }

    private void initializeFields(View view) {
        //find page holder in fragment layout
        viewPager = view.findViewById(R.id.frag_home_vp);
        //pick ui navigation strip ui for tab layout
        mNavigationTabStrip = view.findViewById(R.id.nv_tab_strip);

        //find fab in layout
        mUploadTest = view.findViewById(R.id.fragHome_fab_upload_test);
        mUploadDoc = view.findViewById(R.id.fragHome_fab_upload_doc);
        mPost = view.findViewById(R.id.fragHome_fab_post);
        textViewUsername = view.findViewById(R.id.textViewUsername);

        //set click event in each fab button
        mUploadDoc.setOnClickListener(this);
        mUploadTest.setOnClickListener(this);
        mPost.setOnClickListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //bind button and view all fields
        initializeFields(view);

        setUsername();
        //custom view pager for tab layout into fragment
        // NOTE: must use use getChildFragmentManager inside a fragment to avoid ui collision
        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getActivity(), getChildFragmentManager());
        //set page adapter to viewPager
        viewPager.setAdapter(sectionsPagerAdapter);
        //setting slide animation when tab is switched
        // set up tab Layout with viewPager and tab value
        mNavigationTabStrip.setupWithViewPager(viewPager);
        //data change to tabs is notified
        sectionsPagerAdapter.notifyDataSetChanged();

        return view;
    }

    private void setUsername() {
        if (FirebaseAuth.getInstance().getUid() != null) {
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getUid());
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists() && snapshot.getData() != null && snapshot.getData().containsKey("user_info")) {
                        Map<String, String > data = (Map<String, String>) snapshot.getData().get("user_info");
                        if (data != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
                            textViewUsername.setText("\u2022  "+data.get("user_name") +" \u2022" + "  \n " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        }
                    }
                }
            });
        }
    }

    //defining click events
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragHome_fab_upload_test:
                //open upload test activity
                startActivity(new Intent(getActivity(), UploadTestFile.class));
                break;
            case R.id.fragHome_fab_post:
                break;
            case R.id.fragHome_fab_upload_doc:
                break;
        }
    }
}
