package com.einthefrog.historyquiz.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.einthefrog.historyquiz.databinding.FragmentAnswerCardBinding

class AnswerCardFragment : Fragment() {
    var onOkClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAnswerCardBinding.inflate(layoutInflater)
        binding.textOk.setOnClickListener {
            if (onOkClick != null) {
                onOkClick?.let { onOkClick -> onOkClick() }
            }
        }
        return binding.root
    }
}
