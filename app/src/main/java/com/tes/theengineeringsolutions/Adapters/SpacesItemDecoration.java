package com.tes.theengineeringsolutions.Adapters;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int mSpace;
    public SpacesItemDecoration(int mSpace) {
        this.mSpace = mSpace;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.right = 0;
        outRect.bottom = 24;

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = 0;
        }
        if (parent.getChildAdapterPosition(view) == 1)
            outRect.top = 100;
    }
}
