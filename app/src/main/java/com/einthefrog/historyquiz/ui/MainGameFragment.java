package com.einthefrog.historyquiz.ui;

import android.animation.ValueAnimator;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.einthefrog.historyquiz.databinding.FragmentMainGameBinding;

public class MainGameFragment extends Fragment {
    private static final float cardSensitivity = 0.05f;
    private static final float cardPivotIndent = 500f;
    private static final int cardAnimationDuration = 200;
    private static final float minCardRotationToChooseAnswer = 5f;
    private static final float maxCardRotationToChooseAnswer = 30f;

    private float startMotionX = 0f;
    private QuizCardFragment.Answer highlightedAnswer = QuizCardFragment.Answer.NONE;

    private ValueAnimator rotationAnimation;
    private FragmentMainGameBinding binding;
    private QuizCardFragment quizCardFragment;

    private final View.OnTouchListener quizCardTouchListener = (view, motionEvent) -> {
        CardView quizCardView = (CardView) view;
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (rotationAnimation != null) {
                rotationAnimation.cancel();
            }
            startMotionX = motionEvent.getX();
            quizCardView.performClick();
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            float curMotionX = motionEvent.getX();
            float additionalRotation = horizontalMotionToCardRotation(startMotionX, curMotionX);
            setQuizCardRotation(quizCardView, quizCardView.getRotation() + additionalRotation);
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            animateCardToDefaultPosition();
            return true;
        }
        return false;
    };

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMainGameBinding.inflate(getLayoutInflater());
        binding.quizCardView.setOnTouchListener(quizCardTouchListener);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        quizCardFragment = binding.quizCardFragment.getFragment();
    }

    private void animateCardToDefaultPosition() {
        float defaultRotation = 0f;
        rotationAnimation = ValueAnimator.ofFloat(binding.quizCardView.getRotation(), defaultRotation);
        rotationAnimation.setDuration(cardAnimationDuration);
        rotationAnimation.addUpdateListener(updatedAnimation -> {
            float animatedValue = (float) updatedAnimation.getAnimatedValue();
            setQuizCardRotation(binding.quizCardView, animatedValue);
        });
        rotationAnimation.start();
    }

    private void setQuizCardPivot(View quizCard) {
        quizCard.setPivotY(quizCard.getHeight() + cardPivotIndent);
    }

    private float horizontalMotionToCardRotation(float moveFrom, float moveTo) {
        return (moveTo - moveFrom) * cardSensitivity;
    }

    private void setQuizCardRotation(CardView quizCard, float rotation) {
        setQuizCardPivot(quizCard);
        quizCard.setRotation(rotation);
        setupAnswersHighlighting(rotation);
    }

    private void setupAnswersHighlighting(float cardRotation) {
        cancelAnswerHighlightingIfNecessary(cardRotation);
        highlightAnswerIfNecessary(cardRotation);
    }

    private void highlightAnswerIfNecessary(float cardRotation) {
        QuizCardFragment.Answer answerToHighlight = answerToHighlight(cardRotation);
        boolean noAnswerHighlighted = highlightedAnswer == QuizCardFragment.Answer.NONE;
        boolean canHighlightAnswer = answerToHighlight != QuizCardFragment.Answer.NONE;;
        boolean necessaryToHighlightAnswer = noAnswerHighlighted  && canHighlightAnswer;
        if (necessaryToHighlightAnswer) {
            quizCardFragment.highlightAnswer(answerToHighlight);
            highlightedAnswer = answerToHighlight;
        }
    }

    private void cancelAnswerHighlightingIfNecessary(float cardRotation) {
        QuizCardFragment.Answer answerToHighlight = answerToHighlight(cardRotation);
        boolean someAnswerHighlighted = highlightedAnswer != QuizCardFragment.Answer.NONE;
        boolean noAnswerToHighlight = answerToHighlight == QuizCardFragment.Answer.NONE;;
        boolean necessaryToCancelAnswerHighlighting = someAnswerHighlighted  && noAnswerToHighlight;
        if (necessaryToCancelAnswerHighlighting) {
            quizCardFragment.cancelAnswerHighlighting(highlightedAnswer);
            highlightedAnswer = QuizCardFragment.Answer.NONE;
        }
    }

    private QuizCardFragment.Answer answerToHighlight(float cardRotation) {
        if (cardRotation < -minCardRotationToChooseAnswer) {
            return QuizCardFragment.Answer.LEFT;
        } else if (cardRotation > minCardRotationToChooseAnswer) {
            return QuizCardFragment.Answer.RIGHT;
        } else {
            return QuizCardFragment.Answer.NONE;
        }
    }
}
