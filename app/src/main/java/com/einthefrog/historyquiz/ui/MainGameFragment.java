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

    private float startMotionX = 0f;

    private ValueAnimator animation;
    private FragmentMainGameBinding binding;

    private final View.OnTouchListener quizCardTouchListener = (view, motionEvent) -> {
        CardView quizCardView = (CardView) view;
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (animation != null) {
                animation.cancel();
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
        setQuizCardPivot(binding.quizCardFragment);
    }

    private void animateCardToDefaultPosition() {
        animation = ValueAnimator.ofFloat(binding.quizCardView.getRotation(), 0f);
        animation.setDuration(cardAnimationDuration);
        animation.addUpdateListener(updatedAnimation -> {
            float animatedValue = (float) updatedAnimation.getAnimatedValue();
            setQuizCardRotation(binding.quizCardView, animatedValue);
        });
        animation.start();
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
    }
}
