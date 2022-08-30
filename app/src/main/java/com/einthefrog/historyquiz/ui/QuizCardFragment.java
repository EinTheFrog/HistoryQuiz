package com.einthefrog.historyquiz.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.einthefrog.historyquiz.R;
import com.einthefrog.historyquiz.databinding.FragmentQuizCardBinding;
import com.einthefrog.historyquiz.ui.util.ColorUtil;

public class QuizCardFragment extends Fragment {
    private static final String TAG = "QUIZ_CARD_FRAGMENT";
    private static final float colorAlphaMin = 0f;
    private static final float colorAlphaMax = 1f;
    private static final int animationDuration = 500;

    private FragmentQuizCardBinding binding;
    private Activity activity;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        activity = getActivity();
        assert activity != null;

        binding = FragmentQuizCardBinding.inflate(getLayoutInflater());
        binding.textAnswerLeft.setOnClickListener(view -> {
            highlightAnswer(Answer.LEFT);
        });
        binding.textAnswerRight.setOnClickListener(view -> {
            highlightAnswer(Answer.RIGHT);
        });
        return binding.getRoot();
    }

    public void highlightAnswer(Answer answerType) {
        try {
            TextView answerView = findAnswerView(answerType);
            @ColorInt int highlightColor = getHighlightColorForAnswer(answerType);
            playAnswerHighlightAnimation(answerView, highlightColor);
        } catch (ViewNotFoundException exception) {
            Log.i(TAG, exception.getMessage());
        }
    }

    private TextView findAnswerView(Answer answerType) throws ViewNotFoundException {
        TextView answerView;
        if (answerType == Answer.LEFT) {
            answerView = binding.textAnswerLeft;
        } else if (answerType == Answer.RIGHT) {
            answerView = binding.textAnswerRight;
        } else {
            throw new ViewNotFoundException("Answer view not found for this type of answer: " + answerType);
        }
        return answerView;
    }

    private @ColorInt int getHighlightColorForAnswer(Answer answerType) {
        if (answerType == Answer.LEFT) {
            return ColorUtil.colorWithAlphaFromResource(R.color.blue, 1f, activity);
        } else {
            return ColorUtil.colorWithAlphaFromResource(R.color.orange, 1f, activity);
        }
    }

    private void playAnswerHighlightAnimation(View answerView, @ColorInt int highlightColor) {
        ValueAnimator animation = ValueAnimator.ofFloat(colorAlphaMin, colorAlphaMax);
        animation.addUpdateListener(updatedValue -> {
            float alpha = (float) updatedValue.getAnimatedValue();
            setViewBackgroundColor(answerView, highlightColor, alpha);
        });
        animation.setDuration(animationDuration);
        animation.start();
    }

    private void setViewBackgroundColor(View view, @ColorInt int color, float alpha) {
        @ColorInt int newColor = ColorUtil.colorWithAlpha(color, alpha);
        view.setBackgroundColor(newColor);
    }

    public enum Answer {
        LEFT, RIGHT, NONE
    }

    private static class ViewNotFoundException extends Exception {
        public ViewNotFoundException(String exceptionMessage) {
            super(exceptionMessage);
        }
    }
}
