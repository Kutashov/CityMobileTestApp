package ru.alexandrkutashov.citymobiletestapp

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


/**
 * @author Alexandr Kutashov
 * on 09.11.2019
 */
class MovingCarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        val TAG = MovingCarView::class.java.simpleName
    }

    private val animationTimeInterpolator = LinearInterpolator()

    private var dest: DestinationPoint? = null
    private val car = Car()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        car.move(width / 2f, height - (height / 6f))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            dest?.draw(it)
            car.draw(it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            moveTo(Point(it.x, it.y))
        }
        return true
    }

    private fun moveTo(target: Point) {
        if (target == dest?.point ?: false) {
            return
        }

        dest?.cancel()

        val frontPoint = car.frontPoint
        Log.d(TAG, "curr point: $frontPoint, moveTo $target")

        val angleToDestination = getAngleBetweenVectors(
            frontPoint.x - car.x, frontPoint.y - car.y,
            target.x - car.x, target.y - car.y
        )
        Log.d(TAG, "angle to destination: $angleToDestination")

        val forwardAnimation = getForwardAnimation(target)

        val turningAnimation = getTurningAnimation(angleToDestination)

        dest = DestinationPoint(target, listOf(turningAnimation, forwardAnimation))
    }

    private fun getTurningAnimation(angleToDestination: Angle): Lazy<ValueAnimator> = lazy {
        val startAngle = 0f
        ValueAnimator.ofFloat(startAngle, angleToDestination.value).apply {
            duration = (abs(angleToDestination.value) * 100).toLong() / car.turningSpeed
            interpolator = animationTimeInterpolator
            var lastAngle = startAngle
            addUpdateListener {
                val diff = it.animatedValue as Float - lastAngle
                lastAngle = it.animatedValue as Float

                car.turnBy(Angle(-diff))
                postInvalidateOnAnimation()
            }
        }
    }

    private fun getForwardAnimation(
        target: Point
    ): Lazy<ValueAnimator> = lazy {
        val startDistance = 0f
        val distance = car.frontPoint.getDistanceTo(target)
        Log.d(TAG, "distance to target: $distance")
        ValueAnimator.ofFloat(startDistance, distance).apply {
            duration = (distance * 100).toLong() / car.forwardSpeed
            interpolator = animationTimeInterpolator

            var lastDistance = startDistance
            addUpdateListener {
                val diff = it.animatedValue as Float - lastDistance
                lastDistance = it.animatedValue as Float

                car.moveBy(
                    diff * cos(car.angle.toRadians()),
                    -diff * sin(car.angle.toRadians())
                )
                postInvalidateOnAnimation()
            }
            doOnEnd {
                dest = null
            }
        }
    }
}
