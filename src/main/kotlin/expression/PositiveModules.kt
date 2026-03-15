package tpo.maxim.expression

import tpo.maxim.ln
import tpo.maxim.log
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

private val DEFAULT_MATH_CONTEXT = MathContext.DECIMAL128
private val DEFAULT_EPSILON = BigDecimal("1E-50", DEFAULT_MATH_CONTEXT)

/**
 * Вычисляет: log_10(x) * log_10(x)
 */
fun log10Squared(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val log10x = log(x, BigDecimal(10), epsilon, mc)
    return log10x.multiply(log10x, mc)
}

/**
 * Вычисляет: (log_10(x) * log_10(x)) * log_2(x)
 */
fun multiplyByLog2(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val part1 = log10Squared(x, epsilon, mc)
    val log2x = log(x, BigDecimal(2), epsilon, mc)
    return part1.multiply(log2x, mc)
}

/**
 * Вычисляет: ((log_10(x) * log_10(x)) * log_2(x)) + log_3(x)
 */
fun addLog3(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val part2 = multiplyByLog2(x, epsilon, mc)
    val log3x = log(x, BigDecimal(3), epsilon, mc)
    return part2.add(log3x, mc)
}

/**
 * Вычисляет: (((log_10(x) * log_10(x)) * log_2(x)) + log_3(x)) + log_5(x)
 */
fun addLog5(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val part3 = addLog3(x, epsilon, mc)
    val log5x = log(x, BigDecimal(5), epsilon, mc)
    return part3.add(log5x, mc)
}

/**
 * Вычисляет: ln(x) ^ 3
 */
fun lnCubed(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val lnx = ln(x, epsilon, mc)
    return lnx.multiply(lnx, mc).multiply(lnx, mc)
}

/**
 * Вычисляет полное выражение для x > 0
 */
fun positiveExpression(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    require(x.compareTo(BigDecimal.ZERO) > 0) { "x должен быть > 0" }
    val part4 = addLog5(x, epsilon, mc)
    val part5 = lnCubed(x, epsilon, mc)
    return part4.add(part5, mc)
}
