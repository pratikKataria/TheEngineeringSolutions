package com.tes.theengineeringsolutions.CustomViewPager;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tes.theengineeringsolutions.Fragments.DocumentsFragment;
import com.tes.theengineeringsolutions.Fragments.InboxFragment;
import com.tes.theengineeringsolutions.R;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private FloatingActionButton mFloatingActionButton = null;

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.inbox, R.string.documents};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment fragment = null;

        switch (position)  {
            case 0:
                fragment = new InboxFragment();
                return fragment;
            case 1:
                fragment = new DocumentsFragment();
                return fragment;
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages
        return 2;
    }
}