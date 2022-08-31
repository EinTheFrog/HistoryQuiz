package com.einthefrog.historyquiz.ui;

import android.animation.Animator;
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
        return binding.getRoot();
    }

    public void resetCard() {
        @ColorInt int transparent = ColorUtil.colorWithAlphaFromResource(R.color.transparent, 0f, activity);
        Answer.LEFT.currentAnimationValue = 0f;
        setViewBackgroundColor(binding.textAnswerLeft, transparent, 0f);
        Answer.RIGHT.currentAnimationValue = 0f;
        setViewBackgroundColor(binding.textAnswerRight, transparent, 0f);
    }

    public void highlightAnswer(Answer answerType) {
        try {
            playAnswerHighlightAnimation(answerType, colorAlphaMax);
        } catch (ViewNotFoundException exception) {
            Log.i(TAG, exception.getMessage());
        }
    }

    public void cancelAnswerHighlighting(Answer answerType) {
        try {
            playAnswerHighlightAnimation(answerType, colorAlphaMin);
        } catch (ViewNotFoundException exception) {
            Log.i(TAG, exception.getMessage());
        }
    }

    private void playAnswerHighlightAnimation(Answer answer, float alphaTo) throws ViewNotFoundException {
        View answerView = findAnswerView(answer); // throws ViewNotFoundException
        @ColorInt int highlightColor = getHighlightColorForAnswer(answer);
        if (answer.animation != null) {
            answer.animation.cancel();
        }
        float alphaFrom = answer.currentAnimationValue;
        answer.animation = ValueAnimator.ofFloat(alphaFrom, alphaTo);
        answer.animation.addUpdateListener(updatedValue -> {
            float alpha = (float) updatedValue.getAnimatedValue();
            answer.currentAnimationValue = alpha;
            setViewBackgroundColor(answerView, highlightColor, alpha);
        });
        answer.animation.setDuration(animationDuration);
        answer.animation.start();
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
        return ColorUtil.colorWithAlphaFromResource(answer.colorResourceId, 1f, activity);
    }

    private void setViewBackgroundColor(View view, @ColorInt int color, float alpha) {
        @ColorInt int newColor = ColorUtil.colorWithAlpha(color, alpha);
        view.setBackgroundColor(newColor);
    }

    private static class ViewNotFoundException extends Exception {
        public ViewNotFoundException(String exceptionMessage) {
            super(exceptionMessage);
        }
    }

    public enum Answer {
        LEFT(R.color.blue),
        RIGHT(R.color.orange),
        NONE(R.color.transparent);

        public final int colorResourceId;
        public float currentAnimationValue = colorAlphaMin;
        public ValueAnimator animation;

        Answer(int colorResourceId) {
            this.colorResourceId = colorResourceId;
        }
    }
}
