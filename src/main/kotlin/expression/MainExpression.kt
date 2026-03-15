package expression

import tpo.maxim.expression.positiveExpression

/**
 * Вычисляет значение кусочной функции:
 * - для x <= 0: тригонометрическое выражение
 * - для x > 0: логарифмическое выражение
 */
fun computeExpression(x: Double, epsilon: Double = 1e-10): Double {
    return if (x < 0) {
        negativeExpression(x, epsilon)
    } else {
        positiveExpression(x, epsilon)
    }
}