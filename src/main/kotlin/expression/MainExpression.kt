package expression

import tpo.maxim.expression.positiveExpression

/**
 * Вычисляет значение кусочной функции:
 * - для x <= 0: тригонометрическое выражение
 * - для x > 0: логарифмическое выражение
 */
fun computeExpression(x: Double, epsilon: Double = Double.MIN_VALUE): Double {
    return if (x < 0) {
        negativeExpression(x, epsilon)
    } else {
        positiveExpression(x, epsilon)
    }
}