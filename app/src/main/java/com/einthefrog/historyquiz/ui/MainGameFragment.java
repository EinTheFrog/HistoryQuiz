package com.einthefrog.historyquiz.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.einthefrog.historyquiz.databinding.FragmentMainGameBinding;

public class MainGameFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        FragmentMainGameBinding binding = FragmentMainGameBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
