package com.einthefrog.historyquiz.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.einthefrog.historyquiz.databinding.FragmentMainGameBinding
import com.einthefrog.historyquiz.ui.QuizCardFragment.Answer
import kotlin.math.abs

class MainGameFragment : Fragment() {
    private var quizCardStartMotionX = 0f
    private var answerCardStartMotionX = 0f
    private var quizCardToDefaultAnimation: ValueAnimator? = null
    private var answerCardToDefaultAnimation: ValueAnimator? = null
    private var quizCardOutOfScreenAnimation: ValueAnimator? = null
    private var answerCardOutOfScreenAnimation: ValueAnimator? = null
    private lateinit var binding: FragmentMainGameBinding
    private var quizCardFragment: QuizCardFragment? = null
    private var highlightedAnswer = Answer.NONE
    private val quizCardTouchListener =
        OnTouchListener label@{ view: View, motionEvent: MotionEvent ->
            val quizCardView = view as CardView
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                if (quizCardToDefaultAnimation != null) {
                    quizCardToDefaultAnimation!!.cancel()
                }
                quizCardStartMotionX = motionEvent.x
                quizCardView.performClick()
                return@label true
            } else if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                val curMotionX = motionEvent.x
                val additionalRotation =
                    horizontalMotionToCardRotation(quizCardStartMotionX, curMotionX)
                val rotation = quizCardView.rotation + additionalRotation
                setCardRotation(quizCardView, rotation)
                updateQuizCardAnswersHighlighting(rotation)
                return@label true
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                val rotation = quizCardView.rotation
                val absRotation = Math.abs(rotation)
                if (absRotation < cardRotationToChooseAnswer) {
                    quizCardToDefaultAnimation = animateCardToDefaultPosition(quizCardView, true)
                } else {
                    chooseAnswer(answerToChoose(rotation))
                }
                return@label true
            }
            false
        }
    private val answerCardTouchListener =
        OnTouchListener label@{ view: View, motionEvent: MotionEvent ->
            val answerCardView = view as CardView
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                if (answerCardToDefaultAnimation != null) {
                    answerCardToDefaultAnimation!!.cancel()
                }
                answerCardStartMotionX = motionEvent.x
                answerCardView.performClick()
                return@label true
            } else if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                val curMotionX = motionEvent.x
                val additionalRotation =
                    horizontalMotionToCardRotation(answerCardStartMotionX, curMotionX)
                setCardRotation(answerCardView, answerCardView.rotation + additionalRotation)
                return@label true
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                val absRotation = abs(answerCardView.rotation)
                if (absRotation < cardRotationToChooseAnswer) {
                    answerCardToDefaultAnimation =
                        animateCardToDefaultPosition(answerCardView, false)
                } else {
                    nextQuestion()
                }
                return@label true
            }
            false
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainGameBinding.inflate(layoutInflater)
        binding.quizCardView.setOnTouchListener(quizCardTouchListener)
        binding.answerCardView.setOnTouchListener(answerCardTouchListener)
        binding.quizCardFragment.visibility = View.VISIBLE
        binding.answerCardView.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        quizCardFragment = binding.quizCardFragment.getFragment()
        quizCardFragment!!.setOnAnswerClick { answer: Answer ->
            chooseAnswer(answer)
        }
        val answerCardFragment: AnswerCardFragment = binding.answerCardFragment.getFragment()
        answerCardFragment.onOkClick = this::nextQuestion
    }

    private fun chooseAnswer(answer: Answer) {
        val goneRotation = finalRotationToAnimateCardOutOfScreen(binding.quizCardView, answer)
        quizCardOutOfScreenAnimation = animateCardOutOfScreen(binding.quizCardView, goneRotation)
        if (answerCardOutOfScreenAnimation != null) {
            answerCardOutOfScreenAnimation!!.cancel()
        }
        animateCardAppearance(binding.answerCardView)
    }

    private fun nextQuestion() {
        val goneRotation = finalRotationToAnimateCardOutOfScreen(binding.answerCardView)
        answerCardOutOfScreenAnimation =
            animateCardOutOfScreen(binding.answerCardView, goneRotation)
        if (quizCardOutOfScreenAnimation != null) {
            quizCardOutOfScreenAnimation!!.cancel()
        }
        quizCardFragment!!.resetCard()
        animateCardAppearance(binding.quizCardView)
    }

    private fun animateCardToDefaultPosition(cardView: CardView,
        isQuizCard: Boolean): ValueAnimator {
        val rotationAnimation = ValueAnimator.ofFloat(cardView.rotation, defaultCardRotation)
        rotationAnimation.duration = cardAnimationDuration.toLong()
        rotationAnimation.addUpdateListener { updatedAnimation: ValueAnimator ->
            val animatedValue = updatedAnimation.animatedValue as Float
            setCardRotation(cardView, animatedValue)
            if (isQuizCard) {
                updateQuizCardAnswersHighlighting(animatedValue)
            }
        }
        rotationAnimation.start()
        return rotationAnimation
    }

    private fun animateCardOutOfScreen(cardView: CardView, toRotation: Float): ValueAnimator {
        val fromRotation = cardView.rotation
        val rotationAnimation = ValueAnimator.ofFloat(fromRotation, toRotation)
        rotationAnimation.duration = cardAnimationDuration.toLong()
        rotationAnimation.addUpdateListener { updatedAnimation: ValueAnimator ->
            val animatedValue = updatedAnimation.animatedValue as Float
            setCardRotation(cardView, animatedValue)
        }
        rotationAnimation.start()
        return rotationAnimation
    }

    private fun animateCardAppearance(cardView: CardView): ValueAnimator {
        setCardRotation(cardView, 0f)
        cardView.alpha = 0f
        cardView.visibility = View.VISIBLE
        val alphaAnimation = ValueAnimator.ofFloat(0f, 1f)
        alphaAnimation.duration = cardAnimationDuration.toLong()
        alphaAnimation.addUpdateListener { updatedAnimation: ValueAnimator ->
            val animatedValue = updatedAnimation.animatedValue as Float
            cardView.alpha = animatedValue
        }
        alphaAnimation.start()
        return alphaAnimation
    }

    private fun horizontalMotionToCardRotation(moveFrom: Float, moveTo: Float): Float {
        return (moveTo - moveFrom) * cardSensitivity
    }

    private fun setCardRotation(cardView: CardView, rotation: Float) {
        setCardPivot(cardView)
        cardView.rotation = rotation
    }

    private fun updateQuizCardAnswersHighlighting(rotation: Float) {
        cancelAnswerHighlightingIfNecessary(rotation)
        highlightAnswerIfNecessary(rotation)
    }

    private fun setCardPivot(cardView: View) {
        cardView.pivotY = cardView.height + cardPivotIndent
    }

    private fun highlightAnswerIfNecessary(cardRotation: Float) {
        val answerToHighlight = answerToHighlight(cardRotation)
        val noAnswerHighlighted = highlightedAnswer == Answer.NONE
        val canHighlightAnswer = answerToHighlight != Answer.NONE
        val necessaryToHighlightAnswer = noAnswerHighlighted && canHighlightAnswer
        if (necessaryToHighlightAnswer) {
            quizCardFragment!!.highlightAnswer(answerToHighlight)
            highlightedAnswer = answerToHighlight
        }
    }

    private fun cancelAnswerHighlightingIfNecessary(cardRotation: Float) {
        val answerToHighlight = answerToHighlight(cardRotation)
        val someAnswerHighlighted = highlightedAnswer != Answer.NONE
        val noAnswerToHighlight = answerToHighlight == Answer.NONE
        val necessaryToCancelAnswerHighlighting = someAnswerHighlighted && noAnswerToHighlight
        if (necessaryToCancelAnswerHighlighting) {
            quizCardFragment!!.cancelAnswerHighlighting(highlightedAnswer)
            highlightedAnswer = Answer.NONE
        }
    }

    private fun answerToHighlight(cardRotation: Float): Answer {
        return if (cardRotation < -cardRotationToHighlightAnswer) {
            Answer.LEFT
        } else if (cardRotation > cardRotationToHighlightAnswer) {
            Answer.RIGHT
        } else {
            Answer.NONE
        }
    }

    private fun answerToChoose(cardRotation: Float): Answer {
        return if (cardRotation < -cardRotationToChooseAnswer) {
            Answer.LEFT
        } else if (cardRotation > cardRotationToChooseAnswer) {
            Answer.RIGHT
        } else {
            Answer.NONE
        }
    }

    private fun finalRotationToAnimateCardOutOfScreen(view: CardView, answer: Answer): Float {
        return if (answer == Answer.LEFT) {
            -goneCardRotation
        } else if (answer == Answer.RIGHT) {
            goneCardRotation
        } else {
            finalRotationToAnimateCardOutOfScreen(view)
        }
    }

    private fun finalRotationToAnimateCardOutOfScreen(view: CardView): Float {
        val rotation = view.rotation
        return if (rotation < 0) {
            -goneCardRotation
        } else {
            goneCardRotation
        }
    }

    companion object {
        private const val cardSensitivity = 0.005f
        private const val cardPivotIndent = 5000f
        private const val cardAnimationDuration = 500
        private const val defaultCardRotation = 0f
        private const val cardRotationToHighlightAnswer = 0.5f
        private const val cardRotationToChooseAnswer = 3f
        private const val goneCardRotation = 30f
    }
}
