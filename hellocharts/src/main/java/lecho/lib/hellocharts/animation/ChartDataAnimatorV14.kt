package lecho.lib.hellocharts.animation

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import lecho.lib.hellocharts.view.Chart

class ChartDataAnimatorV14(private val chart: Chart) : ChartDataAnimator, Animator.AnimatorListener,
    AnimatorUpdateListener {
    private val animator: ValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
    private var animationListener: ChartAnimationListener = DummyChartAnimationListener()

    init {
        animator.addListener(this)
        animator.addUpdateListener(this)
    }

    override fun startAnimation(duration: Long) {
        if (duration >= 0) {
            animator.duration = duration
        } else {
            animator.duration = ChartDataAnimator.DEFAULT_ANIMATION_DURATION
        }
        animator.start()
    }

    override fun cancelAnimation() {
        animator.cancel()
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        chart.animationDataUpdate(animation.animatedFraction)
    }

    override fun onAnimationCancel(animation: Animator) {}
    override fun onAnimationEnd(animation: Animator) {
        chart.animationDataFinished()
        animationListener.onAnimationFinished()
    }

    override fun onAnimationRepeat(animation: Animator) {}
    override fun onAnimationStart(animation: Animator) {
        animationListener.onAnimationStarted()
    }

    override val isAnimationStarted: Boolean
        get() = animator.isStarted

    override fun setChartAnimationListener(animationListener: ChartAnimationListener?) {
        if (null == animationListener) {
            this.animationListener = DummyChartAnimationListener()
        } else {
            this.animationListener = animationListener
        }
    }
}