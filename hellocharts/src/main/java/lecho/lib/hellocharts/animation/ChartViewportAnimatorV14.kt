package lecho.lib.hellocharts.animation

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.view.Chart

class ChartViewportAnimatorV14(private val chart: Chart) : ChartViewportAnimator,
    Animator.AnimatorListener, AnimatorUpdateListener {
    private val animator: ValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
    private val startViewport = Viewport()
    private val targetViewport = Viewport()
    private val newViewport = Viewport()
    private var animationListener: ChartAnimationListener = DummyChartAnimationListener()

    init {
        animator.addListener(this)
        animator.addUpdateListener(this)
        animator.duration = ChartViewportAnimator.FAST_ANIMATION_DURATION.toLong()
    }

    override fun startAnimation(startViewport: Viewport, targetViewport: Viewport) {
        this.startViewport.set(startViewport)
        this.targetViewport.set(targetViewport)
        animator.duration = ChartViewportAnimator.FAST_ANIMATION_DURATION.toLong()
        animator.start()
    }

    override fun startAnimation(
        startViewport: Viewport,
        targetViewport: Viewport,
        duration: Long
    ) {
        this.startViewport.set(startViewport)
        this.targetViewport.set(targetViewport)
        animator.duration = duration
        animator.start()
    }

    override fun cancelAnimation() {
        animator.cancel()
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val scale = animation.animatedFraction
        val diffLeft = (targetViewport.left - startViewport.left) * scale
        val diffTop = (targetViewport.top - startViewport.top) * scale
        val diffRight = (targetViewport.right - startViewport.right) * scale
        val diffBottom = (targetViewport.bottom - startViewport.bottom) * scale
        newViewport[startViewport.left + diffLeft, startViewport.top + diffTop, startViewport.right + diffRight] =
            startViewport.bottom + diffBottom
        chart.currentViewport = newViewport
    }

    override fun onAnimationCancel(animation: Animator) {}
    override fun onAnimationEnd(animation: Animator) {
        chart.currentViewport = targetViewport
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