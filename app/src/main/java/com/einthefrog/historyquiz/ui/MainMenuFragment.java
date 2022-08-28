package com.einthefrog.historyquiz.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.einthefrog.historyquiz.R;
import com.einthefrog.historyquiz.databinding.FragmentMainMenuBinding;

public class MainMenuFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        FragmentMainMenuBinding binding = FragmentMainMenuBinding.inflate(getLayoutInflater());

        Activity activity = getActivity();
        if (activity != null) {
            setOnStartClickListener(activity, binding);
        }

        return binding.getRoot();
    }

    private void setOnStartClickListener(Activity activity, FragmentMainMenuBinding binding) {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        binding.buttonStart.setOnClickListener(view -> {
            navController.navigate(R.id.action_mainMenuFragment_to_mainGameFragment);
        });
    }
}
