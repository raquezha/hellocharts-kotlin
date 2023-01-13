package net.raquezha.lecho.hellocharts.animation;

public interface PieChartRotationAnimator {

    int FAST_ANIMATION_DURATION = 200;

    void startAnimation(float startAngle, float angleToRotate);

    void cancelAnimation();

    @SuppressWarnings("unused")
    boolean isAnimationStarted();

    @SuppressWarnings("unused")
    void setChartAnimationListener(ChartAnimationListener animationListener);

}
