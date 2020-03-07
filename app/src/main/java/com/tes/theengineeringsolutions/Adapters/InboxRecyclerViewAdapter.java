package com.tes.theengineeringsolutions.Adapters;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tes.theengineeringsolutions.Models.InboxModel;
import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;

public class InboxRecyclerViewAdapter extends  FirestoreRecyclerAdapter<InboxModel, InboxRecyclerViewAdapter.InboxViewHolder> {

    private static final int EMPTY_VIEW = 0;
    private static final int INBOX_VIEW = 1;


    ArrayList<InboxModel> inboxModelList;
    LayoutInflater inflater;
    Context context;

    // Interface Object
    private InfoAdapterInterface adapterInterface;

    public InboxRecyclerViewAdapter(FirestoreRecyclerOptions<InboxModel> options, Context context) {
        super(options);
        this.context = context;
    }

//    public InboxRecyclerViewAdapter(Context context, ArrayList<InboxModel> inboxModelList, InfoAdapterInterface adapterInterface) {
//        this.context = context;
//        this.inboxModelList = inboxModelList;
//
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        // Initialize your interface to send updates to fragment.
//        this.adapterInterface = adapterInterface;
//    }

    public interface InfoAdapterInterface{
        void OnItemClicked(int item_id);
    }

    @NonNull
    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_inbox, parent, false);
        return new InboxViewHolder(view);
        //        RecyclerView.ViewHolder holder;
//
//        if (viewType == INBOX_VIEW) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_inbox, parent, false);
//            holder = new InboxViewHolder(view);
//        } else {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_layout, parent, false);
//            holder = new EmptyView(view);
//        }
//        return holder;
    }


//    @Override
//    public int getItemViewType(int position) {
////        if (inboxModelList.size() == 0) {
////            return EMPTY_VIEW;
////        } else {
////            return INBOX_VIEW;
////        } \
//        if (getItemCount() > 0) {
//            return INBOX_VIEW;
//        } else {
//            return EMPTY_VIEW;
//        }
//    }

//    @Override
//    public int getItemCount() {
//        if (inboxModelList.size() == 0) {
//            return 1;
//        } else {
//            return inboxModelList.size();
//        }
//    }


    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }

    @Override
    protected void onBindViewHolder(@NonNull InboxViewHolder holder, int position, @NonNull InboxModel model) {
        holder.setCard(
                    model.getHeading(),
                    model.getDescription(),
                    model.getCreated().toString()
        );
        holder.imageButtonDeleteBtn.setOnClickListener(
                v-> holder.deleteDocument(
                        model.getPostId()
                )
        );
    }

//    @Override
//    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull InboxModel model) {
////        switch (holder.getItemViewType()) {
////            case INBOX_VIEW:
////                InboxViewHolder viewHolder = (InboxViewHolder) holder;
////
////                Log.e("Inbox REcycler view ", inboxModelList.toString());
////
////                viewHolder.setCard(
////                        inboxModelList.get(position).getHeading(),
////                        inboxModelList.get(position).getDescription(),
////                        inboxModelList.get(position).getCreated().toString()
////                );
////
////                viewHolder.imageButtonDeleteBtn.setOnClickListener(n -> {
////                    if (inboxModelList.get(position).getPostId() != null)
////                        adapterInterface.OnItemClicked(position);
////                });
////
////                break;
////            case EMPTY_VIEW:
////                break;
////        }
//
//        if (holder instanceof InboxViewHolder) {
//            ((InboxViewHolder) holder).setCard(
//                    model.getHeading(),
//                    model.getDescription(),
//                    model.getCreated().toString()
//            );
//        }
//    }


    public class EmptyView extends RecyclerView.ViewHolder {

        public EmptyView(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class InboxViewHolder extends RecyclerView.ViewHolder {

        TextView textViewHeading;
        TextView textViewDescription;
        TextView textViewDate;
        ImageButton imageButtonDeleteBtn;


        public InboxViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHeading = itemView.findViewById(R.id.inbox_card_layout_tv_heading);
            textViewDescription = itemView.findViewById(R.id.inbox_card_layout_tv_description);
            textViewDate = itemView.findViewById(R.id.inbox_card_layout_tv_date);

            imageButtonDeleteBtn = itemView.findViewById(R.id.inbox_card_layout_ib_delete_btn);
        }

        public void setCard(String heading, String description, String date) {
            textViewHeading.setText(heading);
            textViewDescription.setText(description);
            textViewDate.setText(date);
        }

        void deleteDocument(String docID) {
            DocumentReference seletedDoc = FirebaseFirestore.getInstance().collection("InboxPost").document(docID);
            if (seletedDoc != null) {
                seletedDoc.delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "post deleted", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Toast.makeText(context, "failed to delete post", Toast.LENGTH_SHORT).show());
            }
        }
    }


}
