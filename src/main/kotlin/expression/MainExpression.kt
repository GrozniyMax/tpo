package tpo.maxim.expression

import expression.negativeExpression
import expression.positiveExpression
import java.math.BigDecimal
import java.math.MathContext

private val DEFAULT_MATH_CONTEXT = MathContext.DECIMAL128
private val DEFAULT_EPSILON = BigDecimal("1E-50", DEFAULT_MATH_CONTEXT)

/**
 * Вычисляет значение кусочной функции:
 * - для x <= 0: тригонометрическое выражение
 * - для x > 0: логарифмическое выражение
 */
fun computeExpression(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    return when {
        (x < BigDecimal.ZERO) -> negativeExpression(x, epsilon, mc)
        (x > BigDecimal.ZERO) -> positiveExpression(x, epsilon, mc)
        else -> throw ArithmeticException("function is undefined")
    }
}
