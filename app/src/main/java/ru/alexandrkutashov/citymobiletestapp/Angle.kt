package ru.alexandrkutashov.citymobiletestapp

import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author Alexandr Kutashov
 * on 09.11.2019
 */
inline class Angle(val value: Float)

fun Angle.toRadians(): Float {
    return Math.toRadians(value.toDouble()).toFloat()
}

infix operator fun Int.minus(other: Angle): Float {
    return this - other.value
}

operator fun Angle.plus(other: Angle): Angle {
    return Angle(this.value + other.value)
}

infix operator fun Angle.minus(other: Float): Angle {
    return Angle(this.value - other)
}

fun getAngleBetweenVectors(x1: Double, y1: Double, x2: Double, y2: Double): Angle {
    val num = x1 * x2 + y1 * y2
    val den = sqrt(x1.pow(2.0) + y1.pow(2.0)) *
            sqrt(x2.pow(2.0) + y2.pow(2.0))
    val angle = Math.toDegrees(acos(num / den)).toFloat()

    return if (x1 * y2 - y1 * x2 < 0) {
        Angle(-angle)
    } else {
        Angle(angle)
    }
}

fun getAngleBetweenVectors(x1: Float, y1: Float, x2: Float, y2: Float): Angle {
    return getAngleBetweenVectors(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())
}