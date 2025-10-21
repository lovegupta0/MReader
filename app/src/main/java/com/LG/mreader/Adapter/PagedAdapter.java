package com.LG.mreader.Adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.LG.mreader.DataModel.Page;
import com.LG.mreader.PageActivity.PageFragment;

import java.util.ArrayList;

public class PagedAdapter extends FragmentStateAdapter {
    private ArrayList<Page> pages;

    public PagedAdapter(@NonNull Fragment fragment, ArrayList<Page> pages) {
        super(fragment);
        this.pages = pages == null ? new ArrayList<>() : pages;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return PageFragment.newInstance(pages.get(position));
    }

    @Override
    public int getItemCount() {
        return pages == null ? 0 : pages.size();
    }

    public void setPages(ArrayList<Page> pages) {
        this.pages = pages == null ? new ArrayList<>() : pages;
        notifyDataSetChanged();
    }
}

