package com.einthefrog.historyquiz.ui.game

import android.animation.ValueAnimator
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import com.einthefrog.historyquiz.R
import com.einthefrog.historyquiz.databinding.FragmentQuizCardBinding
import com.einthefrog.historyquiz.ui.util.ColorUtil

class QuizCardFragment : Fragment() {
    private lateinit var binding: FragmentQuizCardBinding
    private var activity: Activity? = null
    private var onAnswerClick: Consumer<Answer>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity = getActivity()
        assert(activity != null)
        binding = FragmentQuizCardBinding.inflate(layoutInflater)
        binding.textAnswerLeft.setOnClickListener {
            highlightAnswer(Answer.LEFT)
            if (onAnswerClick != null) {
                onAnswerClick!!.accept(Answer.LEFT)
            }
        }
        binding.textAnswerRight.setOnClickListener {
            highlightAnswer(Answer.RIGHT)
            if (onAnswerClick != null) {
                onAnswerClick!!.accept(Answer.RIGHT)
            }
        }
        return binding.root
    }

    fun setOnAnswerClick(onAnswerClick: Consumer<Answer>?) {
        this.onAnswerClick = onAnswerClick
    }

    fun resetCard() {
        @ColorInt val transparent = ColorUtil.colorFromResource(activity, R.color.transparent)
        Answer.LEFT.currentAnimationValue = 0f
        setViewBackgroundColor(binding.textAnswerLeft, transparent, 0f)
        Answer.RIGHT.currentAnimationValue = 0f
        setViewBackgroundColor(binding.textAnswerRight, transparent, 0f)
    }

    fun highlightAnswer(answerType: Answer) {
        try {
            playAnswerHighlightAnimation(answerType, colorAlphaMax)
        } catch (exception: ViewNotFoundException) {
            Log.i(TAG, exception.message!!)
        }
    }

    fun cancelAnswerHighlighting(answerType: Answer) {
        try {
            playAnswerHighlightAnimation(answerType, colorAlphaMin)
        } catch (exception: ViewNotFoundException) {
            Log.i(TAG, exception.message!!)
        }
    }

    @Throws(ViewNotFoundException::class)
    private fun playAnswerHighlightAnimation(answer: Answer, alphaTo: Float) {
        val answerView: View = findAnswerView(answer) // throws ViewNotFoundException
        @ColorInt val highlightColor = getHighlightColorForAnswer(answer)
        answer.animation?.cancel()
        val alphaFrom = answer.currentAnimationValue
        answer.animation = ValueAnimator.ofFloat(alphaFrom, alphaTo)
        answer.animation?.addUpdateListener { updatedValue: ValueAnimator ->
            val alpha = updatedValue.animatedValue as Float
            answer.currentAnimationValue = alpha
            setViewBackgroundColor(answerView, highlightColor, alpha)
        }
        answer.animation?.duration = animationDuration.toLong()
        answer.animation?.start()
    }

    @Throws(ViewNotFoundException::class)
    private fun findAnswerView(answerType: Answer): TextView {
        val answerView: TextView = when (answerType) {
            Answer.LEFT -> {
                binding.textAnswerLeft
            }
            Answer.RIGHT -> {
                binding.textAnswerRight
            }
            else -> {
                throw ViewNotFoundException("Answer view not found for this type of answer: $answerType")
            }
        }
        return answerView
    }

    @ColorInt
    private fun getHighlightColorForAnswer(answer: Answer): Int {
        return ColorUtil.colorFromResource(activity, answer.colorResourceId)
    }

    private fun setViewBackgroundColor(view: View, @ColorInt color: Int, alpha: Float) {
        @ColorInt val newColor = ColorUtil.colorWithAlpha(color, alpha)
        view.setBackgroundColor(newColor)
    }

    private class ViewNotFoundException(exceptionMessage: String?) :
        Exception(exceptionMessage)

    enum class Answer(val colorResourceId: Int) {
        LEFT(R.color.blue), RIGHT(R.color.orange), NONE(R.color.transparent);

        var currentAnimationValue = colorAlphaMin
        var animation: ValueAnimator? = null
    }

    companion object {
        private const val TAG = "QUIZ_CARD_FRAGMENT"
        private const val colorAlphaMin = 0f
        private const val colorAlphaMax = 1f
        private const val animationDuration = 500
    }
}
