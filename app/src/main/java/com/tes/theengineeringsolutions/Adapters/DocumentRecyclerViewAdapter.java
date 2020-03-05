package com.tes.theengineeringsolutions.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tes.theengineeringsolutions.BuildConfig;
import com.tes.theengineeringsolutions.Models.NotesModel;
import com.tes.theengineeringsolutions.R;

import java.io.File;
import java.io.IOException;
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

    public DocumentRecyclerViewAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder;

        if (viewType == INBOX_VIEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_notes, parent, false);
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

                viewHolder.imageButtonDownloadFile.setOnClickListener(
                        v -> {
                            viewHolder.downloadFile(inboxModelList.get(position).getFileUri(), inboxModelList.get(position).getNotesId(), inboxModelList.get(position).getFileExtension());
                            Log.e("DocREcycler", inboxModelList.get(position).getFileName() + inboxModelList.get(position).getNotesId());
                        }
                );

                viewHolder.materialCardView.setOnClickListener(v -> {
                    Toast.makeText(context, "card is pressssed", Toast.LENGTH_SHORT).show();
                    try {
                        viewHolder.checkForFile(inboxModelList.get(position).getFileName() +
                                inboxModelList.get(position).getNotesId() + "." +
                                inboxModelList.get(position).getFileExtension() , inboxModelList.get(position).getFileExtension());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                break;
            case EMPTY_VIEW:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (inboxModelList.size() == 0) {
            return EMPTY_VIEW;
        } else {
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
        MaterialCardView materialCardView;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            textviewFileName = itemView.findViewById(R.id.notes_card_tv_file_name);
            textViewDate = itemView.findViewById(R.id.notes_card_tv_date);
            imageButtonDownloadFile = itemView.findViewById(R.id.notes_card_ib_download_file);
            materialCardView = itemView.findViewById(R.id.card_view_notes);
        }

        public void setCard(String fileName, String date) {
            textviewFileName.setText(fileName);
            textViewDate.setText(date);
        }


        void downloadFile(String fileUri, String postUid, String extension) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            Log.e("DocRecycler view Adapter", fileUri);
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(fileUri);

            File rootPath;
            rootPath = new File(Environment.getExternalStorageDirectory(), "/Notes");
            if (!rootPath.exists()) {
                if (!rootPath.mkdirs())
                    Log.d("App ", "Failed to create directory");
                else
                    rootPath.mkdirs();
            }

            final File localFile = new File(rootPath, textviewFileName.getText().toString() + postUid + "." + extension);
            if (localFile.exists()) {
                Toast.makeText(context, "file already present", Toast.LENGTH_SHORT).show();
            } else {
                storageReference.getFile(localFile).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "file downloaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(context, "failed to download file" + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }

        void checkForFile(String FileName, String extension) throws IOException {
            File file = new File(Environment.getExternalStorageDirectory(), "/Notes/" + FileName);
            textViewDate.setText(file.getCanonicalPath());
            Log.e("DocumentRecyclerView Adapter", file.getCanonicalPath() + "");
            if (!file.exists()) {
                Toast.makeText(context, "no file present download file", Toast.LENGTH_SHORT).show();
            } else {
                openFile(file, extension);
            }
        }

        void openFile(File file, String extension) {
            final Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            context.grantUriPermission(context.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            switch(extension) {
                case "doc":
                    intent.setDataAndType(uri, "application/msword");
                    break;
                case "docx" :
                    intent.setDataAndType(uri, "application/msword");
                    break;
                case "pdf":
                    intent.setDataAndType(uri, "application/pdf");
                    break;
                case "xlsx":
                    intent.setDataAndType(uri, "vnd.ms-excel");
                    break;
            }
            intent.setDataAndType(uri, "application/pdf");

            context.startActivity(intent);
        }
    }

}
