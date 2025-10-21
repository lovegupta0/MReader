package com.LG.mreader.PageActivity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.LG.mreader.DataModel.Page;
import com.LG.mreader.Adapter.PagedAdapter;
import com.LG.mreader.R;

import java.util.ArrayList;

public class PagedReaderFragment extends Fragment {
    private ViewPager2 viewPager;
    private PagedAdapter adapter;
    private ArrayList<Page> pages = new ArrayList<>();

    public PagedReaderFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_paged_reader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewPager = view.findViewById(R.id.pager);
        adapter = new PagedAdapter(this, pages);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    public void setPages(ArrayList<Page> pages) {
        this.pages = pages == null ? new ArrayList<>() : pages;
        if (adapter != null) adapter.setPages(this.pages);
    }
}

