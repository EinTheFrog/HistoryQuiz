package com.einthefrog.historyquiz.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.einthefrog.historyquiz.databinding.FragmentAnswerCardBinding;

public class AnswerCardFragment extends Fragment {

    private Runnable onOkClick;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        FragmentAnswerCardBinding binding = FragmentAnswerCardBinding.inflate(getLayoutInflater());
        binding.textOk.setOnClickListener(view -> {
            if (onOkClick != null) {
                onOkClick.run();
            }
        });
        return binding.getRoot();
    }

    public void setOnOkClick(Runnable onOkClick) {
        this.onOkClick = onOkClick;
    }
}
