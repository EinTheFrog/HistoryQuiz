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
            setupAndPlayHighlightAnimation(answerType, colorAlphaMin, colorAlphaMax);
        } catch (ViewNotFoundException exception) {
            Log.i(TAG, exception.getMessage());
        }
    }

    public void cancelAnswerHighlighting(Answer answerType) {
        try {
            setupAndPlayHighlightAnimation(answerType, colorAlphaMax, colorAlphaMin);
        } catch (ViewNotFoundException exception) {
            Log.i(TAG, exception.getMessage());
        }
    }

    private void setupAndPlayHighlightAnimation(
            Answer answerType,
            float alphaFrom,
            float alphaTo
    ) throws ViewNotFoundException {
        TextView answerView = findAnswerView(answerType);
        @ColorInt int highlightColor = getHighlightColorForAnswer(answerType);
        playBackgroundHighlightAnimation(answerView, highlightColor, alphaFrom, alphaTo);
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

    private @ColorInt int getHighlightColorForAnswer(Answer answer) {
        return ColorUtil.colorWithAlphaFromResource(answer.getColorResourceId(), 1f, activity);
    }

    private void playBackgroundHighlightAnimation(
            View answerView,
            @ColorInt int highlightColor,
            float alphaFrom,
            float alphaTo
    ) {
        ValueAnimator animation = ValueAnimator.ofFloat(alphaFrom, alphaTo);
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
        LEFT {
            @Override
            public int getColorResourceId() {
                return R.color.blue;
            }
        },
        RIGHT {
            @Override
            public int getColorResourceId() {
                return R.color.orange;
            }
        },
        NONE {
            @Override
            public int getColorResourceId() {
                return R.color.transparent;
            }
        };
        public abstract int getColorResourceId();
    }

    private static class ViewNotFoundException extends Exception {
        public ViewNotFoundException(String exceptionMessage) {
            super(exceptionMessage);
        }
    }
}
