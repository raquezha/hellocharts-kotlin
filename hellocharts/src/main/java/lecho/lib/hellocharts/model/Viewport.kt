@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package lecho.lib.hellocharts.model

import android.os.Parcel
import android.os.Parcelable
import java.lang.Float.floatToIntBits

/**
 * Partial copy of android.graphics.Rect but here the top should be greater then the bottom.
 * Viewport holds 4 float coordinates for a chart extremes. The viewport is represented
 * by the coordinates of its 4 edges (left, top, right, bottom). These fields can be accessed directly.
 * Use width() and height() to retrieve the viewport's width and height. Note: most methods do not
 * check to see that the coordinates are sorted correctly (i.e. left is less than right and
 * bottom is less than top). Viewport implements Parcelable.
 */
class Viewport : Parcelable {

    @JvmField
    var left = 0f
    @JvmField
    var top = 0f
    @JvmField
    var right = 0f
    @JvmField
    var bottom = 0f

    /**
     * Create a new empty Viewport. All coordinates are initialized to 0.
     */
    constructor()

    /**
     * Create a new viewport with the specified coordinates.
     * Note: no range checking is performed, so the caller must
     * ensure that left is less than right and bottom is less than top.
     *
     * @param left   The X coordinate of the left side of the viewport
     * @param top    The Y coordinate of the top of the viewport
     * @param right  The X coordinate of the right side of the viewport
     * @param bottom The Y coordinate of the bottom of the viewport
     */
    constructor(left: Float, top: Float, right: Float, bottom: Float) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }

    /**
     * Create a new viewport, initialized with the values in the
     * specified viewport (which is left unmodified).
     *
     * @param v The viewport whose coordinates are copied into the new viewport.
     */
    constructor(v: Viewport?) {
        if (v == null) {
            bottom = 0.0f
            right = bottom
            top = right
            left = top
        } else {
            left = v.left
            top = v.top
            right = v.right
            bottom = v.bottom
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val otherClass = other as Viewport
        if (floatToIntBits(bottom) != floatToIntBits(otherClass.bottom)) return false
        if (floatToIntBits(left) != floatToIntBits(otherClass.left)) return false
        if (floatToIntBits(right) != floatToIntBits(otherClass.right)) return false
        return floatToIntBits(top) == floatToIntBits(otherClass.top)
    }

    /**
     * Returns true if the viewport is empty `left >= right or bottom >= top`
     */
    val isEmpty: Boolean
        get() = left >= right || bottom >= top

    /**
     * Set the viewport to (0,0,0,0)
     */
    fun setEmpty() {
        bottom = 0f
        top = bottom
        right = top
        left = right
    }

    /**
     * @return the viewport's width.
     * This does not check for a valid viewport (i.e. `left <= right`) so the result may be negative.
     */
    fun width(): Float {
        return right - left
    }

    /**
     * @return the viewport's height.
     * This does not check for a valid viewport (i.e. `top <= bottom`) so the result may be negative.
     */
    fun height(): Float {
        return top - bottom
    }

    /**
     * @return the horizontal center of the viewport.
     * This does not check for a valid viewport (i.e. `left <= right`)
     */
    fun centerX(): Float {
        return (left + right) * 0.5f
    }

    /**
     * @return the vertical center of the viewport.
     * This does not check for a valid viewport (i.e. `bottom <= top`)
     */
    fun centerY(): Float {
        return (top + bottom) * 0.5f
    }

    /**
     * Set the viewport's coordinates to the specified values. Note: no range checking is performed,
     * so it is up to the caller to ensure that `left <= right and bottom <= top`.
     *
     * @param left   The X coordinate of the left side of the viewport
     * @param top    The Y coordinate of the top of the viewport
     * @param right  The X coordinate of the right side of the viewport
     * @param bottom The Y coordinate of the bottom of the viewport
     */
    operator fun set(left: Float, top: Float, right: Float, bottom: Float) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }

    /**
     * Copy the coordinates from src into this viewport.
     *
     * @param src The viewport whose coordinates are copied into this viewport.
     */
    fun set(src: Viewport) {
        left = src.left
        top = src.top
        right = src.right
        bottom = src.bottom
    }

    /**
     * Offset the viewport by adding dx to its left and right coordinates,
     * and adding dy to its top and bottom coordinates.
     *
     * @param dx The amount to add to the viewport's left and right coordinates
     * @param dy The amount to add to the viewport's top and bottom coordinates
     */
    fun offset(dx: Float, dy: Float) {
        left += dx
        top += dy
        right += dx
        bottom += dy
    }

    /**
     * Offset the viewport to a specific (left, top) position, keeping its width and height the same.
     *
     * @param newLeft The new "left" coordinate for the viewport
     * @param newTop  The new "top" coordinate for the viewport
     */
    fun offsetTo(newLeft: Float, newTop: Float) {
        right += newLeft - left
        bottom += newTop - top
        left = newLeft
        top = newTop
    }

    /**
     * Inset the viewport by (dx,dy). If dx is positive, then the sides are moved inwards,
     * making the viewport narrower. If dx is negative, then the sides are moved outwards,
     * making the viewport wider. The same holds true for dy and the top and bottom.
     *
     * @param dx The amount to add(subtract) from the viewport's left(right)
     * @param dy The amount to add(subtract) from the viewport's top(bottom)
     */
    fun inset(dx: Float, dy: Float) {
        left += dx
        top -= dy
        right -= dx
        bottom += dy
    }

    /**
     * Returns true if (x,y) is inside the viewport. The left and top are considered to be inside,
     * while the right and bottom are not. This means that for a x,y to be contained:
     * `left <= x < right and bottom <= y < top`. An empty viewport never contains any point.
     *
     * @param x The X coordinate of the point being tested for containment
     * @param y The Y coordinate of the point being tested for containment
     * @return true iff (x,y) are contained by the viewport,
     * where containment means `left <= x < right and top <= y < bottom`
     */
    fun contains(x: Float, y: Float): Boolean {
        return left < right && bottom < top && x >= left && x < right && y >= bottom && y < top
    }

    /**
     * Returns true iff the 4 specified sides of a viewport are inside or equal to
     * this viewport. i.e. is this viewport a superset of the specified viewport.
     * An empty viewport never contains another viewport.
     *
     * @param left   The left side of the viewport being tested for containment
     * @param top    The top of the viewport being tested for containment
     * @param right  The right side of the viewport being tested for containment
     * @param bottom The bottom of the viewport being tested for containment
     * @return true iff the the 4 specified sides of a viewport are inside or equal to this viewport
     */
    fun contains(left: Float, top: Float, right: Float, bottom: Float): Boolean {
        // check for empty first
        return this.left < this.right && this.bottom < this.top && this.left <= left && this.top >= top && this.right >= right && this.bottom <= bottom
    }

    /**
     * Returns true iff the specified viewport r is inside or equal to this viewport.
     * An empty viewport never contains another viewport.
     *
     * @param v The viewport being tested for containment.
     * @return true iff the specified viewport r is inside or equal to this viewport
     */
    operator fun contains(v: Viewport): Boolean {
        // check for empty first
        return left < right && bottom < top && left <= v.left && top >= v.top && right >= v.right && bottom <= v.bottom
    }

    /**
     * Update this Viewport to enclose itself and the specified viewport.
     * If the specified viewport is empty, nothing is done. If this viewport is empty it is
     * set to the specified viewport.
     *
     * @param left   The left edge being union with this viewport
     * @param top    The top edge being union with this viewport
     * @param right  The right edge being union with this viewport
     * @param bottom The bottom edge being union with this viewport
     */
    fun union(left: Float, top: Float, right: Float, bottom: Float) {
        if (left < right && bottom < top) {
            if (this.left < this.right && this.bottom < this.top) {
                if (this.left > left) this.left = left
                if (this.top < top) this.top = top
                if (this.right < right) this.right = right
                if (this.bottom > bottom) this.bottom = bottom
            } else {
                this.left = left
                this.top = top
                this.right = right
                this.bottom = bottom
            }
        }
    }

    /**
     * Update this Viewport to enclose itself and the specified viewport.
     * If the specified viewport is empty, nothing is done. If this viewport is empty it is
     * set to the specified viewport.
     *
     * @param v The viewport being unioned with this viewport
     */
    fun union(v: Viewport) {
        union(v.left, v.top, v.right, v.bottom)
    }

    /**
     * If the viewport specified by left,top,right,bottom intersects this viewport,
     * return true and set this viewport to that intersection, otherwise return false
     * and do not change this viewport. No check is performed to see if either viewport is empty.
     * Note: To just test for intersection, use intersects()
     *
     * @param left   The left side of the viewport being intersected with this viewport
     * @param top    The top of the viewport being intersected with this viewport
     * @param right  The right side of the viewport being intersected with this viewport.
     * @param bottom The bottom of the viewport being intersected with this viewport.
     * @return true if the specified viewport and this viewport intersect
     * (and this viewport is then set to that intersection)
     * else return false and do not change this viewport.
     */
    fun intersect(left: Float, top: Float, right: Float, bottom: Float): Boolean {
        if (this.left < right && left < this.right && this.bottom < top && bottom < this.top) {
            if (this.left < left) {
                this.left = left
            }
            if (this.top > top) {
                this.top = top
            }
            if (this.right > right) {
                this.right = right
            }
            if (this.bottom < bottom) {
                this.bottom = bottom
            }
            return true
        }
        return false
    }

    /**
     * If the specified viewport intersects this viewport, return true and set this viewport to that intersection,
     * otherwise return false and do not change this viewport. No check is performed to see if either viewport is empty.
     * To just test for intersection, use intersects()
     *
     * @param v The viewport being intersected with this viewport.
     * @return true if the specified viewport and this viewport intersect (and this viewport is then set to that
     * intersection) else return false and do not change this viewport.
     */
    fun intersect(v: Viewport): Boolean {
        return intersect(v.left, v.top, v.right, v.bottom)
    }

    override fun toString(): String {
        return "Viewport [left=$left, top=$top, right=$right, bottom=$bottom]"
    }

    // ** PARCELABLE **
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + floatToIntBits(bottom)
        result = prime * result + floatToIntBits(left)
        result = prime * result + floatToIntBits(right)
        result = prime * result + floatToIntBits(top)
        return result
    }

    /**
     * Parcelable interface methods
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Write this viewport to the specified parcel. To restore a viewport from a parcel, use readFromParcel()
     *
     * @param out The parcel to write the viewport's coordinates into
     */
    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeFloat(left)
        out.writeFloat(top)
        out.writeFloat(right)
        out.writeFloat(bottom)
    }

    /**
     * Set the viewport's coordinates from the data stored in the specified parcel. To write a viewport to a parcel,
     * call writeToParcel().
     *
     * @param in The parcel to read the viewport's coordinates from
     */
    fun readFromParcel(`in`: Parcel) {
        left = `in`.readFloat()
        top = `in`.readFloat()
        right = `in`.readFloat()
        bottom = `in`.readFloat()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Viewport?> = object : Parcelable.Creator<Viewport?> {
            /**
             * Return a new viewport from the data in the specified parcel.
             */
            override fun createFromParcel(`in`: Parcel): Viewport {
                val v = Viewport()
                v.readFromParcel(`in`)
                return v
            }

            /**
             * Return an array of viewports of the specified size.
             */
            override fun newArray(size: Int): Array<Viewport?> {
                return arrayOfNulls(size)
            }
        }
    }
}