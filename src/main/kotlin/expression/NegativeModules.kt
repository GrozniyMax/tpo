package expression

import basic.*
import java.math.BigDecimal
import java.math.MathContext

private val DEFAULT_MATH_CONTEXT = MathContext.DECIMAL128
private val DEFAULT_EPSILON = BigDecimal("1E-50", DEFAULT_MATH_CONTEXT)

/**
 * Вычисляет ((((csc(x) / csc(x)) / sec(x)) - sin(x)) + cot(x)) ^ 3
 */
fun module1(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val cscx = csc(x, epsilon, mc)
    val secx = sec(x, epsilon, mc)
    val sinx = sin(x, epsilon, mc)
    val cotx = cot(x, epsilon, mc)

    val step1 = cscx.divide(cscx, mc)
    val step2 = step1.divide(secx, mc)
    val step3 = step2.subtract(sinx, mc)
    val step4 = step3.add(cotx, mc)

    return step4.multiply(step4, mc).multiply(step4, mc)
}

/**
 * Вычисляет cos(x) + csc(x)
 */
fun module2(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val cosx = cos(x, epsilon, mc)
    val cscx = csc(x, epsilon, mc)
    return cosx.add(cscx, mc)
}

/**
 * Вычисляет sec(x) - sin(x)
 */
fun module3(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val secx = sec(x, epsilon, mc)
    val sinx = sin(x, epsilon, mc)
    return secx.subtract(sinx, mc)
}

/**
 * Вычисляет ((...) + (...)) - (...)
 */
fun module4Numerator(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val module1Val = module1(x, epsilon, mc)
    val module2Val = module2(x, epsilon, mc)
    val module3Val = module3(x, epsilon, mc)

    return module1Val.add(module2Val, mc).subtract(module3Val, mc)
}

/**
 * Вычисляет знаменатель (cot(x) ^ 2) / (csc(x) + sec(x))
 */
fun module4Denominator(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val cotx = cot(x, epsilon, mc)
    val cscx = csc(x, epsilon, mc)
    val secx = sec(x, epsilon, mc)

    val cotSquared = cotx.multiply(cotx, mc)
    val cscSecSum = cscx.add(secx, mc)

    return cotSquared.divide(cscSecSum, mc)
}

/**
 * Вычисляет дробь: (...)/(...)
 */
fun module5(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val numerator = module4Numerator(x, epsilon, mc)
    val denominator = module4Denominator(x, epsilon, mc)
    return numerator.divide(denominator, mc)
}


/**
 * Вычисляет знаменатель всей большой дроби
 */
fun module6Numerator(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    val mainFraction = module5(x, epsilon, mc)
    val sinx = sin(x, epsilon, mc)
    val sinSquared = sinx.multiply(sinx, mc)

    val product = mainFraction.multiply(sinSquared, mc)
    return product.multiply(product, mc)
}

/**
 * Полное выражение для x <= 0
 */
fun negativeExpression(x: BigDecimal, epsilon: BigDecimal = DEFAULT_EPSILON, mc: MathContext = DEFAULT_MATH_CONTEXT): BigDecimal {
    require(x <= BigDecimal.ZERO) { "x должен быть <= 0" }

    val module6 = module6Numerator(x, epsilon, mc)
    val cotx = cot(x, epsilon, mc)

    return module6.divide(cotx, mc)
}
