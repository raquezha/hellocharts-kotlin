package lecho.lib.hellocharts.animation

interface PieChartRotationAnimator {
    fun startAnimation(startAngle: Float, angleToRotate: Float)
    fun cancelAnimation()
    val isAnimationStarted: Boolean
    fun setChartAnimationListener(animationListener: ChartAnimationListener?)

    companion object {
        const val FAST_ANIMATION_DURATION = 200
    }
}