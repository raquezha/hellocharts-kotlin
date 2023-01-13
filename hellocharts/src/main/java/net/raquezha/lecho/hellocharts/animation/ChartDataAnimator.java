package net.raquezha.lecho.hellocharts.animation;

public interface ChartDataAnimator {

    long DEFAULT_ANIMATION_DURATION = 500;

    void startAnimation(long duration);

    void cancelAnimation();

    @SuppressWarnings("unused")
    boolean isAnimationStarted();

    void setChartAnimationListener(ChartAnimationListener animationListener);

}
