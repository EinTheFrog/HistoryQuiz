package com.einthefrog.historyquiz.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.einthefrog.historyquiz.databinding.FragmentQuizCardBinding;

public class QuizCardFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        FragmentQuizCardBinding binding = FragmentQuizCardBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
