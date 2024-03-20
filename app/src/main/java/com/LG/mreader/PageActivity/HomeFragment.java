package com.LG.mreader.PageActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.LG.mreader.MainActivity;
import com.LG.mreader.R;
import com.LG.mreader.ViewModel.WebViewModel;
import com.LG.mreader.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding home;
    private WebViewModel webViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        home= FragmentHomeBinding.inflate(inflater, container, false);
        webViewModel= new ViewModelProvider(requireActivity()).get(WebViewModel.class);
        webViewModel.getUrlAddress().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                home.txt.setText(s);
            }
        });
        
        return home.getRoot();
    }
}