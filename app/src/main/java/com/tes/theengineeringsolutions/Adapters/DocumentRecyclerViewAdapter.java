package com.tes.theengineeringsolutions.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;
import com.tes.theengineeringsolutions.Models.NotesModel;
import com.tes.theengineeringsolutions.R;



import java.util.ArrayList;

public class DocumentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int EMPTY_VIEW = 0;
    private static final int INBOX_VIEW = 1;

    ArrayList<NotesModel> inboxModelList;
    LayoutInflater inflater;
    Context context;

    public DocumentRecyclerViewAdapter(Context context, ArrayList<NotesModel> inboxModelList) {
        this.context = context;
        this.inboxModelList = inboxModelList;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public DocumentRecyclerViewAdapter() {}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder;

        if (viewType == INBOX_VIEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_card, parent,false);
            holder = new NotesViewHolder(view);
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
                NotesViewHolder viewHolder = (NotesViewHolder) holder;

                viewHolder.setCard(
                        inboxModelList.get(position).getFileName(),
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

    public class NotesViewHolder extends RecyclerView.ViewHolder {

        TextView textviewFileName;
        TextView textViewDate;

        ImageButton imageButtonDownloadFile;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            textviewFileName = itemView.findViewById(R.id.notes_card_tv_file_name);
            textViewDate = itemView.findViewById(R.id.notes_card_tv_date);
            imageButtonDownloadFile = itemView.findViewById(R.id.notes_card_ib_download_file);
        }

        public void setCard(String fileName, String date) {
            textviewFileName.setText(fileName);
            textViewDate.setText(date);
        }
    }

}
