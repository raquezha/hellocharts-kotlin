package net.raquezha.lecho.hellocharts.animation;

import net.raquezha.lecho.hellocharts.model.Viewport;

public interface ChartViewportAnimator {

    int FAST_ANIMATION_DURATION = 300;

    void startAnimation(Viewport startViewport, Viewport targetViewport);

    void startAnimation(Viewport startViewport, Viewport targetViewport, long duration);

    void cancelAnimation();

    @SuppressWarnings("unused")
    boolean isAnimationStarted();

    void setChartAnimationListener(ChartAnimationListener animationListener);

}
