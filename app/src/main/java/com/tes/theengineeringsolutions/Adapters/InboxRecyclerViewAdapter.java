package com.tes.theengineeringsolutions.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.tes.theengineeringsolutions.Models.InboxModel;
import com.tes.theengineeringsolutions.R;

public class InboxRecyclerViewAdapter extends FirestoreRecyclerAdapter<InboxModel, InboxRecyclerViewAdapter.InboxViewHolder> {

    Context context;

    // Interface Object
    private InboxAdapterListener inboxAdapterListener;

    public InboxRecyclerViewAdapter(FirestoreRecyclerOptions<InboxModel> options, Context context, InboxAdapterListener inboxAdapterListener) {
        super(options);
        this.context = context;
        this.inboxAdapterListener = inboxAdapterListener;
    }

    @NonNull
    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_inbox, parent, false);
        return new InboxViewHolder(view);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (getItemCount() == 0) {
            inboxAdapterListener.onEmptyStateListener(true);
        } else {
            inboxAdapterListener.onEmptyStateListener(false);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull InboxViewHolder holder, int position, @NonNull InboxModel model) {
        holder.setCard(
                model.getHeading(),
                model.getDescription(),
                model.getCreated().toString()
        );

        holder.imageButtonDeleteBtn.setOnClickListener (v -> holder.deleteDocument());
    }

    public interface InboxAdapterListener {
        void onEmptyStateListener(boolean isEmpty);
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

        void deleteDocument() {
            getSnapshots().getSnapshot(getAdapterPosition()).getReference().delete();
        }
    }


}
