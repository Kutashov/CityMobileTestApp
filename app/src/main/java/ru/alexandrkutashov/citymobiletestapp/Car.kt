package ru.alexandrkutashov.citymobiletestapp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author Alexandr Kutashov
 * on 09.11.2019
 */
class Car(val forwardSpeed: Int = 15, val turningSpeed: Int = 5) : Drawable {

    companion object {
        private const val CAR_WIDTH = 100
        private const val CAR_LENGTH = 200
    }

    val frontPoint: Point
        get() = Point(
            x + CAR_LENGTH * cos(angle.toRadians()),
            y - CAR_LENGTH * sin(angle.toRadians())
        )

    private val carBitmap = getCarBitmap()
    private val rotationMatrix = Matrix()

    internal var x = 0f
    internal var y = 0f
    internal var angle = Angle(90f)

    override fun draw(canvas: Canvas) {
        rotationMatrix.reset()
        rotationMatrix.setRotate(
            90 - angle,
            carBitmap.width / 2f,
            carBitmap.height / 2f
        )
        rotationMatrix.postTranslate(x - carBitmap.width / 2, y - carBitmap.height / 2)
        canvas.drawBitmap(carBitmap, rotationMatrix, null)
    }

    fun move(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun moveBy(dx: Float, dy: Float){
        x += dx
        y += dy
    }

    fun turnBy(angle: Angle) {
        this.angle = this.angle + angle
    }

    private fun getCarBitmap(): Bitmap {
        val bitmap =
            Bitmap.createBitmap(CAR_WIDTH, CAR_LENGTH, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.YELLOW)

        val wheelWidth = 10
        val wheelLength = 40
        val spaceToRear = 30
        val wheelColor = Color.RED

        // Wheels
        for (j in 0 until wheelWidth) {
            for (i in spaceToRear until wheelLength + spaceToRear) {
                bitmap.setPixel(j, i, wheelColor)
            }
        }

        for (j in 0 until wheelWidth) {
            for (i in CAR_LENGTH - spaceToRear - wheelLength until CAR_LENGTH - spaceToRear) {
                bitmap.setPixel(j, i, wheelColor)
            }
        }

        for (j in CAR_WIDTH - wheelWidth until CAR_WIDTH) {
            for (i in spaceToRear until wheelLength + spaceToRear) {
                bitmap.setPixel(j, i, wheelColor)
            }
        }

        for (j in CAR_WIDTH - wheelWidth until CAR_WIDTH) {
            for (i in CAR_LENGTH - spaceToRear - wheelLength until CAR_LENGTH - spaceToRear) {
                bitmap.setPixel(j, i, wheelColor)
            }
        }

        //Lights
        for (j in 0 until CAR_WIDTH) {
            for (i in 0 until wheelWidth) {
                bitmap.setPixel(j, i, Color.BLACK)
            }
        }

        return bitmap
    }
}
