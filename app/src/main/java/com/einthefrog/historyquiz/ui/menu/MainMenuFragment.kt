package com.einthefrog.historyquiz.ui.menu

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.einthefrog.historyquiz.R
import com.einthefrog.historyquiz.databinding.FragmentMainMenuBinding

class MainMenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentMainMenuBinding = FragmentMainMenuBinding.inflate(layoutInflater)
        val activity: Activity? = activity
        activity?.let { setOnStartClickListener(it, binding) }
        return binding.root
    }

    private fun setOnStartClickListener(activity: Activity, binding: FragmentMainMenuBinding) {
        val navController = findNavController(activity, R.id.nav_host_fragment)
        binding.buttonStart.setOnClickListener { navController.navigate(R.id.action_mainMenuFragment_to_mainGameFragment) }
    }
}
