package basic

import java.math.BigDecimal
import java.math.MathContext

private val DEFAULT_MATH_CONTEXT = MathContext.DECIMAL128

/**
 * Вычисляет натуральный логарифм ln(x) с использованием ряда:
 * ln(x) = 2 * Σ((t^(2n+1)) / (2n+1)) для n=0 до ∞, где t = (x-1)/(x+1)
 */
fun ln(x: BigDecimal, epsilon: BigDecimal = BigDecimal("1E-50", DEFAULT_MATH_CONTEXT), mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    if (x.compareTo(BigDecimal.ZERO) <= 0) {
        throw IllegalArgumentException("x must be positive, got: $x")
    }

    val t = x.subtract(BigDecimal.ONE, mc).divide(x.add(BigDecimal.ONE, mc), mc)
    val tSquared = t.multiply(t, mc)
    
    var result = BigDecimal.ZERO
    var term = t
    var n = 1
    
    while (term.abs().compareTo(epsilon) >= 0) {
        val divisor = BigDecimal(2 * n - 1)
        result = result.add(term.divide(divisor, mc), mc)
        term = term.multiply(tSquared, mc)
        n++
    }
    
    return BigDecimal(2).multiply(result, mc)
}

/**
 * Вычисляет логарифм log_base(x) = ln(x) / ln(base)
 */
fun log(x: BigDecimal, base: BigDecimal, epsilon: BigDecimal = BigDecimal("1E-50", DEFAULT_MATH_CONTEXT), mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    if (x.compareTo(BigDecimal.ZERO) <= 0 || base.compareTo(BigDecimal.ZERO) <= 0 || base.compareTo(BigDecimal.ONE) == 0) {
        throw IllegalArgumentException("x and base must be positive, and base must not be 1")
    }
    val lnX = ln(x, epsilon, mc)
    val lnBase = ln(base, epsilon, mc)
    return lnX.divide(lnBase, mc)
}
