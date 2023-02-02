package lecho.lib.hellocharts.animation

import lecho.lib.hellocharts.model.Viewport

interface ChartViewportAnimator {
    fun startAnimation(startViewport: Viewport, targetViewport: Viewport)
    fun startAnimation(startViewport: Viewport, targetViewport: Viewport, duration: Long)
    fun cancelAnimation()
    val isAnimationStarted: Boolean
    fun setChartAnimationListener(animationListener: ChartAnimationListener?)

    companion object {
        const val FAST_ANIMATION_DURATION = 300
    }
}