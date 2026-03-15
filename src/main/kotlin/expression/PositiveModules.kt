package tpo.maxim.expression

import tpo.maxim.ln
import tpo.maxim.log

/**
 * Вычисляет: log_10(x) * log_10(x)
 */
fun log10Squared(x: Double, epsilon: Double = Double.MIN_VALUE): Double {
    val log10x = log(x, 10.0, epsilon)
    return log10x * log10x
}

/**
 * Вычисляет: (log_10(x) * log_10(x)) * **log_2(x)**
 */
fun multiplyByLog2(x: Double, epsilon: Double = Double.MIN_VALUE): Double {
    val part1 = log10Squared(x, epsilon)
    val log2x = log(x, 2.0, epsilon)
    return part1 * log2x
}

/**
 * Вычисляет: ((log_10(x) * log_10(x)) * log_2(x)) + **log_3(x)**
 */
fun addLog3(x: Double, epsilon: Double = Double.MIN_VALUE): Double {
    val part2 = multiplyByLog2(x, epsilon)
    val log3x = log(x, 3.0, epsilon)
    return part2 + log3x
}

/**
 * Вычисляет: (((log_10(x) * log_10(x)) * log_2(x)) + log_3(x)) + **log_5(x)**
 */
fun addLog5(x: Double, epsilon: Double = Double.MIN_VALUE): Double {
    val part3 = addLog3(x, epsilon)
    val log5x = log(x, 5.0, epsilon)
    return part3 + log5x
}

/**
 * Вычисляет: ln(x) ^ 3
 */
fun lnCubed(x: Double, epsilon: Double = Double.MIN_VALUE): Double {
    val lnx = ln(x, epsilon)
    return lnx * lnx * lnx
}

/**
 * Вычисляет полное выражение для x > 0
 */
fun positiveExpression(x: Double, epsilon: Double = Double.MIN_VALUE): Double {
    require(x > 0) { "x должен быть > 0" }
    val part4 = addLog5(x, epsilon)
    val part5 = lnCubed(x, epsilon)
    return part4 + part5
}