package lecho.lib.hellocharts.view.hack

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.drawerlayout.widget.DrawerLayout

/**
 * Hacky fix for issue with DrawerLayout [...](https://github.com/chrisbanes/PhotoView/issues/72)
 */
class HackyDrawerLayout : DrawerLayout {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    )

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return try {
            super.onInterceptTouchEvent(ev)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}