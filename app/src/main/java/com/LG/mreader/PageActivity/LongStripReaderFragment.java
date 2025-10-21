package com.LG.mreader.PageActivity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.LG.mreader.Adapter.LongStripAdapter;
import com.LG.mreader.DataModel.Page;
import com.LG.mreader.R;

import java.util.ArrayList;

public class LongStripReaderFragment extends Fragment {
    private RecyclerView recyclerView;
    private LongStripAdapter adapter;
    private ArrayList<Page> pages = new ArrayList<>();

    public LongStripReaderFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_long_strip_reader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.long_strip_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new LongStripAdapter(requireContext(), pages);
        recyclerView.setAdapter(adapter);
    }

    public void setPages(ArrayList<Page> pages) {
        this.pages = pages == null ? new ArrayList<>() : pages;
        if (adapter != null) adapter.setPages(this.pages);
    }
}

