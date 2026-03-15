package tpo.maxim

import kotlin.math.PI
import kotlin.math.abs

fun cos(x: Double, epsilon: Double = Double.MIN_VALUE): Double {
    var result = 0.0
    var term = 1.0
    var n = 0

    while (abs(term) >= epsilon) {
        result += term
        n++
        term *= -x * x / ((2 * n - 1) * 2 * n)
    }

    return result
}

fun sin(x: Double, epsilon: Double = Double.MIN_VALUE): Double {
    val piOver2 = PI / 2
    return cos(piOver2 - x, epsilon)
}

fun sec(x: Double, epsilon: Double = Double.MIN_VALUE): Double {
    return 1.0 / cos(x, epsilon)
}

fun csc(x: Double, epsilon: Double = Double.MIN_VALUE): Double {
    return 1.0 / sin(x, epsilon)
}

fun cot(x: Double, epsilon: Double = Double.MIN_VALUE): Double {
    return cos(x, epsilon) / sin(x, epsilon)
}