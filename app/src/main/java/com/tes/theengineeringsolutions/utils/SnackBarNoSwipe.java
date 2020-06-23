package com.tes.theengineeringsolutions.utils;

import android.view.View;
import com.google.android.material.snackbar.BaseTransientBottomBar;

public class SnackBarNoSwipe extends BaseTransientBottomBar.Behavior {
    @Override
    public boolean canSwipeDismissView(View child) {
        return false;
    }
}
