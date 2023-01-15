package lecho.lib.hellocharts.animation

interface ChartDataAnimator {
    fun startAnimation(duration: Long)
    fun cancelAnimation()
    val isAnimationStarted: Boolean
    fun setChartAnimationListener(animationListener: ChartAnimationListener?)

    companion object {
        const val DEFAULT_ANIMATION_DURATION: Long = 500
    }
}