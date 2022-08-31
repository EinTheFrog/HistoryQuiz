package com.einthefrog.historyquiz.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.einthefrog.historyquiz.databinding.FragmentMainGameBinding;

public class MainGameFragment extends Fragment {
    private static final float cardSensitivity = 0.005f;
    private static final float cardPivotIndent = 5000f;
    private static final int cardAnimationDuration = 200;
    private static final float defaultCardRotation = 0f;
    private static final float cardRotationToHighlightAnswer = 0.5f;
    private static final float cardRotationToChooseAnswer = 3f;
    private static final float goneCardRotation = 30f;

    private float quizCardStartMotionX = 0f;
    private float answerCardStartMotionX = 0f;

    private ValueAnimator quizCardToDefaultAnimation;
    private ValueAnimator answerCardToDefaultAnimation;
    private FragmentMainGameBinding binding;
    private QuizCardFragment quizCardFragment;
    private QuizCardFragment.Answer highlightedAnswer = QuizCardFragment.Answer.NONE;

    private final View.OnTouchListener quizCardTouchListener = (view, motionEvent) -> {
        CardView quizCardView = (CardView) view;
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (quizCardToDefaultAnimation != null) {
                quizCardToDefaultAnimation.cancel();
            }
            quizCardStartMotionX = motionEvent.getX();
            quizCardView.performClick();
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            float curMotionX = motionEvent.getX();
            float additionalRotation = horizontalMotionToCardRotation(quizCardStartMotionX, curMotionX);
            float rotation = quizCardView.getRotation() + additionalRotation;
            setCardRotation(quizCardView, rotation);
            setCardAnswersHighlight(rotation);
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            float absRotation = Math.abs(quizCardView.getRotation());
            if (absRotation < cardRotationToChooseAnswer) {
                quizCardToDefaultAnimation = animateCardToDefaultPosition(quizCardView);
            } else {
                chooseAnswer();
            }
            return true;
        }
        return false;
    };

    private final View.OnTouchListener answerCardTouchListener = (view, motionEvent) -> {
        CardView answerCardView = (CardView) view;
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (answerCardToDefaultAnimation != null) {
                answerCardToDefaultAnimation.cancel();
            }
            answerCardStartMotionX = motionEvent.getX();
            answerCardView.performClick();
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            float curMotionX = motionEvent.getX();
            float additionalRotation = horizontalMotionToCardRotation(answerCardStartMotionX, curMotionX);
            setCardRotation(answerCardView, answerCardView.getRotation() + additionalRotation);
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            float absRotation = Math.abs(answerCardView.getRotation());
            if (absRotation < cardRotationToChooseAnswer) {
                quizCardToDefaultAnimation = animateCardToDefaultPosition(answerCardView);
            } else {
                nextQuestion();
            }
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
        binding.answerCardView.setOnTouchListener(answerCardTouchListener);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        quizCardFragment = binding.quizCardFragment.getFragment();
    }

    private void chooseAnswer() {
        animateCardOutOfScreen(binding.quizCardView);
    }

    private void nextQuestion() {
        animateCardOutOfScreen(binding.answerCardView);
    }

    private ValueAnimator animateCardToDefaultPosition(CardView cardView) {
        ValueAnimator rotationAnimator = ValueAnimator.ofFloat(cardView.getRotation(), defaultCardRotation);
        rotationAnimator.setDuration(cardAnimationDuration);
        rotationAnimator.addUpdateListener(updatedAnimation -> {
            float animatedValue = (float) updatedAnimation.getAnimatedValue();
            setCardRotation(cardView, animatedValue);
        });
        rotationAnimator.start();
        return rotationAnimator;
    }

    private ValueAnimator animateCardOutOfScreen(CardView cardView) {
        float fromRotation = cardView.getRotation();
        float toRotation = goneCardRotation;
        if (fromRotation < 0) {
            toRotation = -goneCardRotation;
        }
        ValueAnimator rotationAnimator = ValueAnimator.ofFloat(fromRotation, toRotation);
        rotationAnimator.setDuration(cardAnimationDuration);
        rotationAnimator.addUpdateListener(updatedAnimation -> {
            float animatedValue = (float) updatedAnimation.getAnimatedValue();
            setCardRotation(cardView, animatedValue);
        });
        rotationAnimator.start();
        return rotationAnimator;
    }

    private float horizontalMotionToCardRotation(float moveFrom, float moveTo) {
        return (moveTo - moveFrom) * cardSensitivity;
    }

    private void setCardRotation(CardView cardView, float rotation) {
        setCardPivot(cardView);
        cardView.setRotation(rotation);
    }

    private void setCardAnswersHighlight(float rotation) {
        cancelAnswerHighlightingIfNecessary(rotation);
        highlightAnswerIfNecessary(rotation);
    }

    private void setCardPivot(View cardView) {
        cardView.setPivotY(cardView.getHeight() + cardPivotIndent);
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
        if (cardRotation < -cardRotationToHighlightAnswer) {
            return QuizCardFragment.Answer.LEFT;
        } else if (cardRotation > cardRotationToHighlightAnswer) {
            return QuizCardFragment.Answer.RIGHT;
        } else {
            return QuizCardFragment.Answer.NONE;
        }
    }
}
