package com.tes.theengineeringsolutions.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.widget.TextView;

import com.tes.theengineeringsolutions.Models.InboxModel;
import com.tes.theengineeringsolutions.R;

import java.util.ArrayList;

public class InboxRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int EMPTY_VIEW = 0;
    private static final int INBOX_VIEW = 1;

    ArrayList<InboxModel> inboxModelList;
    LayoutInflater inflater;
    Context context;

    public InboxRecyclerViewAdapter(Context context, ArrayList<InboxModel> inboxModelList) {
        this.context = context;
        this.inboxModelList = inboxModelList;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public InboxRecyclerViewAdapter() {}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder;

        if (viewType == INBOX_VIEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_inbox, parent,false);
            holder = new InboxViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_layout, parent, false);
            holder = new EmptyView(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case INBOX_VIEW:
                InboxViewHolder viewHolder = (InboxViewHolder) holder;

                viewHolder.setCard(
                        inboxModelList.get(position).getHeading(),
                        inboxModelList.get(position).getDescription(),
                        inboxModelList.get(position).getCreated().toString()
                );


                break;
            case EMPTY_VIEW:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (inboxModelList.size() == 0 ) {
            return EMPTY_VIEW;
        }  else {
            return INBOX_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        if (inboxModelList.size() == 0) {
            return 1;
        } else {
            return inboxModelList.size();
        }
    }

    public class EmptyView extends RecyclerView.ViewHolder {

        public EmptyView(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class InboxViewHolder extends RecyclerView.ViewHolder {

        TextView textViewHeading;
        TextView textViewDescription;
        TextView textViewDate;


        public InboxViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHeading = itemView.findViewById(R.id.inbox_card_layout_tv_heading);
            textViewDescription = itemView.findViewById(R.id.inbox_card_layout_tv_description);
            textViewDate = itemView.findViewById(R.id.inbox_card_layout_tv_date);
        }

        public void setCard(String heading, String description, String date) {
            textViewHeading.setText(heading);
            textViewDescription.setText(description);
            textViewDate.setText(date);
        }
    }

}
