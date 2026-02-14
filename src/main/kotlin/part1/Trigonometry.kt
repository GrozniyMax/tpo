package tpo.maxim.part1

import kotlin.Int
import kotlin.math.abs
import kotlin.math.sqrt

private const val PI = 3.141592653589793


fun myArcsin(x: Double, n: Int = Int.MAX_VALUE, eps: Double = Double.MIN_VALUE): Double {

    if (abs(x) > 1.0) return Double.NaN
    if (x == -0.0 || x.isNaN()) return x

    // Выражаем через тригонометрическое тождество arcsin(x) = PI/2 - arcsin(sqrt(1 - x^2))
    if (abs(x) >= 0.999999) {
        val sqrtTerm = sqrt(1.0 - x * x)
        val result = myArcsin(sqrtTerm, n)
        return if (x > 0) PI / 2 - result else -PI / 2 + result
    }

    var result = x
    var term = x

    for (i in 0 .. n) {
        term = calculateNextTerm(term, x, i)
        result += term

        // Проверка на сходимость
        if (abs(term) < eps) break
    }

    return result
}

/**
 * Вычисляет следующий член ряда Тейлора для функции arcsin(x)
 * на основе текущего члена.
 */
private fun calculateNextTerm(currentTerm: Double, x: Double, i: Int): Double {
    var nextTerm = currentTerm
    nextTerm *= ((2 * i + 1) * (2 * i + 2)).toDouble()
    nextTerm /= 4.0 * (i + 1) * (i + 1) * (2 * i + 3)
    nextTerm *= x * x * (2 * i + 1)
    return nextTerm
}