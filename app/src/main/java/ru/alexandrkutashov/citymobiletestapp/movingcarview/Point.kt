package ru.alexandrkutashov.citymobiletestapp.movingcarview

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.animation.doOnEnd
import kotlin.math.sqrt

/**
 * @author Alexandr Kutashov
 * on 09.11.2019
 */
class DestinationPoint(val point: Point, private val animations: List<Lazy<ValueAnimator>>): Drawable by point {
    init {
        val onAnimationEndAction: (Animator) -> Unit = {
            animations.find { !it.isInitialized() }?.value!!.start()
        }
        animations[0].value.apply {
            doOnEnd { onAnimationEndAction(it) }
            start()
        }
    }

    fun cancel() {
        animations.forEach {
            if (it.isInitialized()) {
                it.value.cancel()
            }
        }
    }

    override fun draw(canvas: Canvas) {
        animations.find { it.value.isRunning }?.let { point.draw(canvas) }
    }
}

data class Point(val x: Float, val y: Float) : Drawable {

    companion object {
        const val MIN_DIFF_DISTANCE = 20
    }

    private val style = Paint(Paint.ANTI_ALIAS_FLAG)
        .also {
            it.style = Paint.Style.FILL
            it.color = Color.GREEN
        }

    private val radius = 50f

    override fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, radius, style)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Point) return false

        if (getDistance(x, y, other.x, other.y) > MIN_DIFF_DISTANCE) {
            return false
        }

        return true
    }
}

fun Point.getDistanceTo(other: Point): Float {
    return getDistance(x, y, other.x, other.y)
}

fun getDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    return sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)))
}