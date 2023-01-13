package net.raquezha.lecho.hellocharts.view.hack;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Hacky fix for issue with DrawerLayout <a href="https://github.com/chrisbanes/PhotoView/issues/72">...</a>
 */
public class HackyDrawerLayout extends DrawerLayout {

    public HackyDrawerLayout(Context context) {
        super(context);
    }

    public HackyDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HackyDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
