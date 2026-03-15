package tpo.maxim

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

private val DEFAULT_MATH_CONTEXT = MathContext.DECIMAL128

/**
 * Вычисляет cos(x) с использованием ряда Тейлора
 * cos(x) = Σ((-1)^n * x^(2n) / (2n)!) для n=0 до ∞
 */
fun cos(x: BigDecimal, epsilon: BigDecimal = BigDecimal("1E-50", DEFAULT_MATH_CONTEXT), mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val xMod = x.remainder(BigDecimal(2 * Math.PI).setScale(50, RoundingMode.HALF_UP), mc)
    
    var result = BigDecimal.ZERO
    var term = BigDecimal.ONE
    var n = 0

    while (term.abs().compareTo(epsilon) >= 0) {
        result = result.add(term, mc)
        n++
        val numerator = term.multiply(xMod.multiply(xMod, mc).negate(mc), mc)
        val denominator = BigDecimal((2 * n - 1) * 2 * n)
        term = numerator.divide(denominator, mc)
    }

    return result
}

/**
 * Вычисляет sin(x) через cos(π/2 - x)
 */
fun sin(x: BigDecimal, epsilon: BigDecimal = BigDecimal("1E-50", DEFAULT_MATH_CONTEXT), mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val piOver2 = BigDecimal(Math.PI / 2).setScale(50, RoundingMode.HALF_UP)
    val adjustedX = piOver2.subtract(x, mc)
    return cos(adjustedX, epsilon, mc)
}

/**
 * Вычисляет sec(x) = 1 / cos(x)
 */
fun sec(x: BigDecimal, epsilon: BigDecimal = BigDecimal("1E-50", DEFAULT_MATH_CONTEXT), mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val cosX = cos(x, epsilon, mc)
    return BigDecimal.ONE.divide(cosX, mc)
}

/**
 * Вычисляет csc(x) = 1 / sin(x)
 */
fun csc(x: BigDecimal, epsilon: BigDecimal = BigDecimal("1E-50", DEFAULT_MATH_CONTEXT), mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val sinX = sin(x, epsilon, mc)
    return BigDecimal.ONE.divide(sinX, mc)
}

/**
 * Вычисляет cot(x) = cos(x) / sin(x)
 */
fun cot(x: BigDecimal, epsilon: BigDecimal = BigDecimal("1E-50", DEFAULT_MATH_CONTEXT), mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val cosX = cos(x, epsilon, mc)
    val sinX = sin(x, epsilon, mc)
    return cosX.divide(sinX, mc)
}
